package net.ndrei.teslacorelib.tileentities

import com.google.common.collect.Lists
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagByte
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.AxisAlignedBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.capabilities.ICapabilityAddon
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.capabilities.container.IGuiContainerProvider
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider
import net.ndrei.teslacorelib.gui.*
import net.ndrei.teslacorelib.inventory.*
import net.ndrei.teslacorelib.items.BaseAddon
import net.ndrei.teslacorelib.items.TeslaWrench
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslacorelib.render.HudInfoLine
import net.ndrei.teslacorelib.render.HudInfoRenderer
import net.ndrei.teslacorelib.render.IHudInfoProvider
import net.ndrei.teslacorelib.utils.fillFrom
import net.ndrei.teslacorelib.utils.processInputInventory
import net.ndrei.teslacorelib.utils.withAlpha
import java.awt.Color
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Created by CF on 2017-06-27.
 */
@Suppress("MemberVisibilityCanPrivate")
abstract class SidedTileEntity protected constructor(entityTypeId: Int)
    : SyncTileEntity(entityTypeId), IHudInfoProvider, IGuiContainerProvider, ITeslaWrenchHandler, IRedstoneControlledMachine {
    protected val sideConfig: SidedItemHandlerConfig

    private val itemHandler: SidedItemHandler

    protected val fluidHandler: SidedFluidHandler
    private var fluidItems: ItemStackHandler? = null
    protected var addonItems: ItemStackHandler? = null

    private var wasPickedUpInItemStack = false

    private var paused = false
    private var redstoneSetting = IRedstoneControlledMachine.RedstoneControl.AlwaysActive

    private val fakeSyncTarget = object : ISyncTarget {
        override fun partialSync(key: String) {
            this@SidedTileEntity.partialSync(key)
        }

        override fun forceSync() {
            this@SidedTileEntity.forceSync()
        }
    }

    init {
        this.sideConfig = object : SidedItemHandlerConfig() {
            override fun updated() {
                this@SidedTileEntity.partialSync(SYNC_SIDE_CONFIG)
                this@SidedTileEntity.notifyNeighbours()
            }
        }
        this.registerSyncListPart(SYNC_SIDE_CONFIG, this.sideConfig)

        this.itemHandler = SidedItemHandler(this.sideConfig)

        @Suppress("LeakingThis") // trust me, I know what I'm doing! :S
        this.fluidHandler = SidedFluidHandler(this.sideConfig, this)
        this.registerSyncTagPart(SYNC_FLUIDS, this.fluidHandler, this.getDefaultFluidSyncLevel())

        this.registerSyncStringPart(SYNC_REDSTONE_CONTROL,
            Consumer { this.redstoneSetting = IRedstoneControlledMachine.RedstoneControl.valueOf(it.string) },
            Supplier { NBTTagString(this.redstoneSetting.name) })

        this.registerSyncBytePart(SYNC_IS_PAUSED,
            Consumer { this.paused = this.canBePaused() && (it.byte != 0.toByte()) },
            Supplier { NBTTagByte(if (this.paused) 1 else 0) })

        this.initializeInventories()
        this.ensureFluidItems()
    }

    protected open fun getDefaultFluidSyncLevel() = SyncProviderLevel.GUI
    protected open fun getDefaultItemsSyncLevel() = SyncProviderLevel.GUI

    //region inventory         methods

    protected open fun initializeInventories() {
        this.createAddonsInventory()
    }

    protected fun addSimpleInventory(stacks: Int, storageKey: String, color: EnumDyeColor, displayName: String,
                                     boundingBox: BoundingRectangle,
                                     inputFilter: ((stack: ItemStack, slot: Int) -> Boolean)? = null,
                                     outputFilter: ((stack: ItemStack, slot: Int) -> Boolean)? = null,
                                     lockable: Boolean = false, colorIndex: Int? = null): IItemHandlerModifiable {
        val handler = when(lockable) {
            true -> LockableItemHandler(stacks)
            false -> SyncItemHandler(stacks)
        }

        this.addInventory(object : ColoredItemHandler(handler, color, displayName, colorIndex, boundingBox) {
            override fun canInsertItem(slot: Int, stack: ItemStack) =
                (if (slot in 0 until this.slots) {
                    if (inputFilter != null) inputFilter(stack, slot)
                    else true
                }
                else false) && ((this.innerHandler as? IFilteredItemHandler)?.canInsertItem(slot, stack) ?: true)

            override fun canExtractItem(slot: Int) =
                (if (slot in 0 until this.slots) {
                    if (outputFilter != null) outputFilter(this.getStackInSlot(slot), slot)
                    else true
                }
                else false) && ((this.innerHandler as? IFilteredItemHandler)?.canExtractItem(slot) ?: true)
        })

        this.addInventoryToStorage(handler, storageKey)
        return handler
    }

    protected fun addInventory(handler: IItemHandler) {
        this.itemHandler.addItemHandler(handler)

        if (handler is ColoredItemHandler) {
            if (handler.index == null) {
                this.sideConfig.addColoredInfo(handler.name, handler.color, handler.boundingBox)
            }
            else {
                this.sideConfig.addColoredInfo(handler.name, handler.color, handler.boundingBox, handler.index)
            }
        }
    }

    protected fun addInventoryToStorage(handler: ItemStackHandler, storageKey: String) {
        if (handler is ISyncProvider) {
            handler.setSyncTarget(this.fakeSyncTarget, storageKey)
        }
        this.registerSyncTagPart(storageKey, handler, this.getDefaultItemsSyncLevel())
    }

    open fun getInventoryLockState(color: EnumDyeColor): Boolean? {
        val inventory = (0 until this.itemHandler.inventories)
                .map { this.itemHandler.getInventory(it) }
                .firstOrNull { (it is ColoredItemHandler) && (it.color == color) }

        if ((inventory != null) && (inventory is ColoredItemHandler)) {
            val inner = inventory.innerHandler
            if (inner is LockableItemHandler) {
                return inner.locked
            }
        }
        return null
    }

    open fun toggleInventoryLock(color: EnumDyeColor) {
        val inventory = (0 until this.itemHandler.inventories)
                .map { this.itemHandler.getInventory(it) }
                .firstOrNull { (it is ColoredItemHandler) && (it.color == color) }

        if ((inventory != null) && (inventory is ColoredItemHandler)) {
            val inner = inventory.innerHandler
            if (inner is LockableItemHandler) {
                inner.locked = !inner.locked

                if (TeslaCoreLib.isClientSide) {
                    val message = this.setupSpecialNBTMessage("TOGGLE_LOCK")
                    message.setInteger("color", color.metadata)
                    this.sendToServer(message)
                }
            }
        }
    }

    protected open fun processImmediateInventories() {
        if (this.fluidItems != null) {
            this.processFluidItems(this.fluidItems!!)
        }
    }

    protected fun getInventory(color: EnumDyeColor) =
        (0 until this.itemHandler.inventories)
            .map { this.itemHandler.getInventory(it) }
            .filterIsInstance<ColoredItemHandler>()
            .firstOrNull { it.color == color }

    //endregion

    //region inventory addons  methods

    private fun createAddonsInventory() {
        if (this.supportsAddons()) {
            this.addonItems = object : ItemStackHandler(this.supportedAddonColumns * 4) {
                private val items = (0 until this.slots).map<Int, ItemStack?> { null }.toTypedArray()

                override fun onContentsChanged(slot: Int) {
                    this.testSlot(slot)
                }

                override fun onLoad() {
                    for (index in 0 until this.slots) {
                        this.testSlot(index)
                    }
                }

                private fun testSlot(slot: Int) {
                    // TODO: refactor this!
                    var stack = this.getStackInSlot(slot).copy()
                    var item: Item? = if (stack.isEmpty) null else stack.item
                    if (item !is BaseAddon) {
                        item = null
                    } else {
                        stack = stack.copy()
                    }

                    if (item == null && this.items[slot] != null) {
                        (this.items[slot]!!.item as BaseAddon).onRemoved(this.items[slot]!!, this@SidedTileEntity)
                        this.items[slot] = null
                    } else if (item != null && this.items[slot] == null) {
                        this.items[slot] = stack
                        (item as BaseAddon).onAdded(this.items[slot]!!, this@SidedTileEntity)
                    } else if (item != null && this.items[slot] != null && !ItemStack.areItemStacksEqual(this.items[slot], stack)) {
                        (this.items[slot]!!.item as BaseAddon).onRemoved(this.items[slot]!!, this@SidedTileEntity)
                        this.items[slot] = stack
                        (item as BaseAddon).onAdded(this.items[slot]!!, this@SidedTileEntity)
                    }

                    this@SidedTileEntity.partialSync("addonItems")
                }

                override fun getSlotLimit(slot: Int): Int {
                    return 1
                }
            }
            this.addInventory(object : ColoredItemHandler(this.addonItems!!, EnumDyeColor.BLACK, "Addons", BoundingRectangle.EMPTY) {
                override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                    return this@SidedTileEntity.isValidAddonItem(stack)
                }

                override fun canExtractItem(slot: Int): Boolean {
                    return false
                }

                override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                    val slots = super.getSlots(container)
                    (0 until this.slots).mapTo(slots) {
                        val column = it / 4
                        val row = it % 4
                        FilteredSlot(this.itemHandlerForContainer, it, 174 - column * 18, 8 + row * 18)
                    }
                    return slots
                }

                override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                    val pieces = super.getGuiContainerPieces(container)

                    (0 until this@SidedTileEntity.supportedAddonColumns).forEach {
                        pieces.add(TiledRenderedGuiPiece(173 - it * 18, 7, 18, 18, 1, 4,
                            BasicTeslaGuiContainer.MACHINE_BACKGROUND, 144, 190, null))
                    }

                    return pieces
                }
            })
            this.registerSyncTagPart("addonItems", this.addonItems!!, this.getDefaultItemsSyncLevel())
        }
    }

    protected open fun supportsAddons() = true

    protected open val supportedAddonColumns get() = 1

    protected fun <T : BaseAddon> getAddon(addonClass: Class<T>?): T? {
        if (this.addonItems != null && addonClass != null) {
            val addon = (0 until this.addonItems!!.slots)
                .map { this.addonItems!!.getStackInSlot(it) }
                .filterNot { it.isEmpty }
                .map { it.item }
                .firstOrNull { addonClass.isAssignableFrom(it.javaClass) }
                ?: return null
            @Suppress("UNCHECKED_CAST")
            return addon as T
        }
        return null
    }

    protected fun <T : BaseAddon> getAddonStack(addonClass: Class<T>?): ItemStack {
        if (this.addonItems != null && addonClass != null) {
            val addon = (0 until this.addonItems!!.slots)
                .map { this.addonItems!!.getStackInSlot(it) }
                .firstOrNull { !it.isEmpty && addonClass.isAssignableFrom(it.item.javaClass) }
            return addon ?: ItemStack.EMPTY
        }
        return ItemStack.EMPTY
    }

    protected fun <T : BaseAddon> hasAddon(addonClass: Class<T>): Boolean {
        return null != this.getAddon(addonClass)
    }

    val addons: List<BaseAddon>
        get() {
            val list = mutableListOf<BaseAddon>()

            if (this.addonItems != null) {
                list += (0 until this.addonItems!!.slots)
                        .map { this.addonItems!!.getStackInSlot(it) }
                        .filterNot { it.isEmpty }
                        .map { it.item }
                        .filterIsInstance<BaseAddon>()
            }

            return list
        }

    protected fun isValidAddonItem(stack: ItemStack): Boolean {
        if (!stack.isEmpty) {
            val item = stack.item
            if (item is BaseAddon) {
                if (item.canBeAddedTo(this)) {
                    return true
                }
            }
        }
        return false
    }

    fun removeAddon(addon: BaseAddon, drop: Boolean): ItemStack {
        if (this.addonItems != null && !this.getWorld().isRemote) {
            for (index in 0 until this.addonItems!!.slots) {
                val stack = this.addonItems!!.getStackInSlot(index)
                if (!stack.isEmpty && (stack.item === addon)) {
                    this.addonItems!!.setStackInSlot(index, ItemStack.EMPTY)
                    if (drop) {
                        this.spawnItemFromFrontSide(stack)
                    }
                    return stack
                }
            }
        }

        return ItemStack.EMPTY
    }

    //endregion

    //region fluid tank        methods

    protected fun addFluidTank(filter: Fluid, capacity: Int, color: EnumDyeColor?, name: String?, boundingBox: BoundingRectangle?): IFluidTank {
        val tank = object : FluidTank(capacity) {
            override fun onContentsChanged() {
//                this@SidedTileEntity.markDirty()
//                this@SidedTileEntity.forceSync()
                this@SidedTileEntity.partialSync(SYNC_FLUIDS)
            }
        }
        val colored = this.fluidHandler.addTank(filter, tank, color ?: EnumDyeColor.BLACK, name ?: "yup, this is a thing!", boundingBox ?: BoundingRectangle.EMPTY)

        if (color != null && name != null && name.isNotEmpty() && boundingBox != null) {
            this.sideConfig.addColoredInfo(name, color, boundingBox)
        }

        return colored.innerTank
    }

    protected fun addFluidTank(tank: IFluidTank, box: BoundingRectangle?) {
        this.fluidHandler.addTank(tank)

        if (tank is ColoredFluidHandler) {
            val tankBox = box ?: tank.boundingBox
            if (!tankBox.isEmpty) {
                this.sideConfig.addColoredInfo(tank.name, tank.color, tankBox)
            }
        }
    }

    protected fun addFluidTank(tank: IFluidTank, color: EnumDyeColor?, name: String?, boundingBox: BoundingRectangle?)
        = this.addFluidTank(tank, color, name, boundingBox, false)

    protected fun addFluidTank(tank: IFluidTank, color: EnumDyeColor?, name: String?, boundingBox: BoundingRectangle?, outputOnly: Boolean) {
        if (color != null && name != null && name.isNotEmpty() && boundingBox != null) {
            if (outputOnly) {
                this.fluidHandler.addTank(object: ColoredFluidHandler(tank, color, name, boundingBox) {
                    override fun acceptsFluid(fluid: FluidStack) = false
                })
            }
            else {
                this.fluidHandler.addTank(ColoredFluidHandler(tank, color, name, boundingBox))
            }
            this.sideConfig.addColoredInfo(name, color, boundingBox)
        } else {
            this.fluidHandler.addTank(tank)
        }
    }

//    protected fun addSimpleFluidTank(capacity: Int, name: String, color: EnumDyeColor, guiLeft: Int, guiTop: Int, tankType: FluidTankType): IFluidTank
//        = this.addSimpleFluidTank(capacity, name, color, guiLeft, guiTop, tankType, { tankType.canFill }, { tankType.canDrain })

    protected fun addSimpleFluidTank(capacity: Int, name: String, color: EnumDyeColor, guiLeft: Int, guiTop: Int,
                                     tankType: FluidTankType = FluidTankType.BOTH,
                                     externalFillFilter: ((FluidStack) -> Boolean)? = null,
                                     externalDrainFilter: ((FluidStack) -> Boolean)? = null): IFluidTank {
        val tank = object: FluidTank(capacity) {
            override fun onContentsChanged() {
//                this@SidedTileEntity.markDirty()
//                this@SidedTileEntity.forceSync()
                this@SidedTileEntity.partialSync(SYNC_FLUIDS)
            }
        }

        this.addFluidTank(object: ColoredFluidHandler(tank, color, name, BoundingRectangle.fluid(guiLeft, guiTop)) {
            override fun acceptsFluid(fluid: FluidStack) = if (externalFillFilter == null) this.tankType.canFill else externalFillFilter(fluid)
            override fun canDrain() = this.fluid.let { if (it == null) false else if (externalDrainFilter == null) this.tankType.canDrain else externalDrainFilter(it) }
        }.also { it.tankType = tankType }, null)

        return tank
    }

    protected fun removeFluidTank(color: EnumDyeColor, tank: IFluidTank) {
        this.fluidHandler.removeTank(tank)
        this.sideConfig.removeColoredInfo(color)
        this.markDirty()
    }

    protected val colorForFluidInventory: EnumDyeColor?
        get() = EnumDyeColor.SILVER

    protected fun ensureFluidItems() {
        if (!shouldAddFluidItemsInventory())
            return

        if (this.fluidItems == null) {
            val color = this.colorForFluidInventory
            if (color != null) {
                val box = this.fluidItemsBoundingBox

                this.fluidItems = object : ItemStackHandler(2) {
                    override fun onContentsChanged(slot: Int) {
                        this@SidedTileEntity.partialSync(SYNC_FLUID_ITEMS)
                    }
                }
                this.addInventory(object : ColoredItemHandler(this.fluidItems!!, color, "$MOD_ID:Fluid Containers", this.fluidItemsColorIndex, box) {
                    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                        return slot == 0 && this@SidedTileEntity.acceptsFluidItem(stack)
                    }

                    override fun canExtractItem(slot: Int): Boolean {
                        return slot != 0
                    }

                    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                        val slots = mutableListOf<Slot>()

                        val bounds = this.boundingBox
                        if (!bounds.isEmpty) {
                            slots.add(FilteredSlot(this.itemHandlerForContainer, 0, bounds.left + 1, bounds.top + 1))
                            slots.add(FilteredSlot(this.itemHandlerForContainer, 1, bounds.left + 1, bounds.top + 1 + 36))
                        }

                        return slots
                    }

                    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                        val pieces = mutableListOf<IGuiContainerPiece>()

                        val bounds = this.boundingBox
                        if (!bounds.isEmpty) {
                            this@SidedTileEntity.addFluidItemsBackground(pieces, bounds)
                        }

                        return pieces
                    }
                })
                this.registerSyncTagPart(SYNC_FLUID_ITEMS, this.fluidItems!!)
            }
        }
    }

    protected open fun acceptsFluidItem(stack: ItemStack): Boolean {
        return this.fluidHandler.acceptsFluidFrom(stack)
    }

    protected open val fluidItemsBoundingBox: BoundingRectangle
        get() {
            var x = 0
            var y = 0
            for (tank in this.fluidHandler.getTanks()) {
                if (tank is ColoredFluidHandler) {
                    val box = tank.boundingBox
                    x = Math.max(x, box.right)
                    y = box.top
                }
            }
            return BoundingRectangle(x, y, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)
        }

    protected open val fluidItemsColorIndex: Int? = null

    protected open fun addFluidItemsBackground(pieces: MutableList<IGuiContainerPiece>, box: BoundingRectangle) {
        pieces.add(BasicRenderedGuiPiece(box.left, box.top, 18, 54,
                BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189))
    }

    protected open fun shouldAddFluidItemsInventory(): Boolean = this.fluidHandler.tankCount() != 0

    protected open fun processFluidItems(fluidItems: ItemStackHandler) {
        this.fluidHandler.processInputInventory(fluidItems)
    }

    @Deprecated("Replaced by a kotlin extension to IItemHandlerModifiable.", ReplaceWith("IItemHandlerModifiable.discardUsedItem()"))
    protected fun discardUsedFluidItem() {
        if (this.fluidItems != null) {
            val source = this.fluidItems!!.getStackInSlot(0)
            val result = this.fluidItems!!.insertItem(1, source, false)
            this.fluidItems!!.setStackInSlot(0, result)
        }
    }

    fun handleBucket(player: EntityPlayer, hand: EnumHand/*, side: EnumFacing*/): Boolean {
        val bucket = player.getHeldItem(hand)
        if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val handler = ItemStackHandler(2)
            handler.setStackInSlot(0, bucket.copy())
            handler.setStackInSlot(1, ItemStack.EMPTY)
            if (this.fluidHandler.processInputInventory(handler)) {
                if (!player.capabilities.isCreativeMode) {
                    val result = handler.getStackInSlot(if (handler.getStackInSlot(0).isEmpty) 1 else 0)
                    player.setHeldItem(hand, result)
                }
                return true
            }
        }
        return false
    }

    //endregion

    //region storage           methods

    val facing: EnumFacing
        get() {
            val state = this.getWorld().getBlockState(this.getPos())
            if (state.block is OrientedBlock<*>) {
                return state.getValue(AxisAlignedBlock.FACING)
            }
            return EnumFacing.NORTH
        }

    //#endregion

    //#region sync & network    methods

    override fun processClientMessage(messageType: String?, player: EntityPlayerMP?, compound: NBTTagCompound): SimpleNBTMessage? {
        when(messageType) {
            "FILL_TANK" -> {
                if (player != null) {
                    val color = if (compound.hasKey("color", Constants.NBT.TAG_INT))
                        EnumDyeColor.byMetadata(compound.getInteger("color"))
                    else return null
                    val stack = player.inventory.itemStack
                    if (!stack.isEmpty) {
                        val tank = this.fluidHandler
                                .getTanks()
                                .filterIsInstance<ColoredFluidHandler>()
                                .firstOrNull { it.color == color }
                        if (tank != null) {
                            player.inventory.itemStack = tank.fillFrom(stack)
                            player.connection.sendPacket(SPacketSetSlot(-1, 0, player.inventory.itemStack))
                            this.partialSync(SYNC_FLUIDS)
                        }
                    }
                }
                else {
                    TeslaCoreLib.logger.error("Cannot fill a tank without a player!")
                }
                return null
            }
            "DRAIN_TANK" -> {
                if (player != null) {
                    val color = if (compound.hasKey("color", Constants.NBT.TAG_INT))
                        EnumDyeColor.byMetadata(compound.getInteger("color"))
                    else return null
                    val stack = player.inventory.itemStack
                    if (!stack.isEmpty) {
                        val tank = this.fluidHandler
                                .getTanks()
                                .filterIsInstance<ColoredFluidHandler>()
                                .firstOrNull { it.color == color }
                                .let {
                                    if ((it is ITypedFluidTank) && (it.tankType == FluidTankType.INPUT))
                                        it.innerTank
                                    else
                                        it
                                }
                        if (tank != null) {
                            player.inventory.itemStack = stack.fillFrom(tank)
                            player.connection.sendPacket(SPacketSetSlot(-1, 0, player.inventory.itemStack))
                            this.partialSync(SYNC_FLUIDS)
                        }
                    }
                }
                else {
                    TeslaCoreLib.logger.error("Cannot fill a tank without a player!")
                }
                return null
            }
        }
        return super.processClientMessage(messageType, player, compound)
    }

    override fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? {
        if (messageType == "TOGGLE_SIDE") {
            val color = EnumDyeColor.byMetadata(compound.getInteger("color"))
            val facing = EnumFacing.getFront(compound.getInteger("side"))
            this.sideConfig.toggleSide(color, facing)
//            this.markDirty()
//            this@SidedTileEntity.forceSync()
        }
        else if (messageType == "TOGGLE_LOCK") {
            val color = EnumDyeColor.byMetadata(compound.getInteger("color"))
            this.toggleInventoryLock(color)
        }
        else if (messageType == "PAUSE_MACHINE") {
            val pause = compound.getBoolean("pause")
            if (pause != this.isPaused()) {
                this.togglePause()
            }
        }
        else if (messageType == "REDSTONE_CONTROL") {
            this.redstoneSetting = IRedstoneControlledMachine.RedstoneControl.valueOf(compound.getString("setting"))
        }

        return super.processClientMessage(messageType, compound)
    }

    //endregion

    //region capability & info methods

    protected fun orientFacing(facing: EnumFacing?): EnumFacing {
        if (facing == null) {
            return EnumFacing.NORTH
        }

        if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
            return facing
        }

        val machineFacing = this.facing
        if (machineFacing == EnumFacing.EAST) {
            return facing.rotateY()
        }
        if (machineFacing == EnumFacing.NORTH) {
            return facing.opposite // .rotateY().rotateY();
        }
        if (machineFacing == EnumFacing.WEST) {
            return facing.rotateYCCW()
        }
        return facing
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        val oriented = this.orientFacing(facing)

        if (capability === TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER) {
            return true
        } else if (capability === TeslaCoreCapabilities.CAPABILITY_WRENCH) {
            return true
        }

        if (!this.isPaused()) {
            if (this.supportsAddons() && !this.isProcessingCapAddons) {
                try {
                    this.isProcessingCapAddons = true
                    this.addons.filterIsInstance<ICapabilityAddon>().forEach {
                        val hasCap = it.hasCapability(this, capability, facing, oriented)
                        if (hasCap != null) {
                            return hasCap
                        }
                    }
                }
                finally { this.isProcessingCapAddons = false }
            }

            if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                val slots = this.itemHandler.getSlotsForFace(oriented)
                return slots.isNotEmpty()
            } else if (this.fluidHandler.hasCapability(capability, oriented)) {
                return true
            }
        }

        return super.hasCapability(capability, facing)
    }

    private var isProcessingCapAddons = false

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val oriented = this.orientFacing(facing)

        if (this.supportsAddons() && !this.isProcessingCapAddons) {
            try {
                this.isProcessingCapAddons = true
                this.addons.filterIsInstance<ICapabilityAddon>().forEach {
                    val cap = it.getCapability(this, capability, facing, oriented)
                    if (cap != null) {
                        return cap
                    }
                }
            }
            finally { this.isProcessingCapAddons = false }
        }

        return when {
            capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> this.itemHandler.getSideWrapper(oriented) as T
            capability === TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER -> this as T
            capability === TeslaCoreCapabilities.CAPABILITY_WRENCH -> this as T
            else -> this.fluidHandler.getCapability(capability, oriented) ?: super.getCapability(capability, facing)
        }
    }

    override fun getHudLines(face: EnumFacing?): List<HudInfoLine> {
        return listOf(*(if ((face != null) && Minecraft.getMinecraft().player.isSneaking) {
            arrayOf(HudInfoLine(Color.LIGHT_GRAY, Color.LIGHT_GRAY.withAlpha(.24f), "Side: ${this.getSideDirection(face).toUpperCase()} (${face.toString().capitalize()})"))
        } else arrayOf()), *this.hudLines.toTypedArray())
    }

    protected fun getSideDirection(face: EnumFacing)
            = when (this.facing) {
        face -> "front"
        face.opposite -> "back"
        face.rotateY() -> "right"
        face.rotateYCCW() -> "left"
        else -> "[error]"
    }

    protected open val hudLines: MutableList<HudInfoLine>
        get() = if (this.isPaused())
            mutableListOf(
                HudInfoLine(Color.RED, Color.RED.withAlpha(.24f), SYNC_IS_PAUSED).setTextAlignment(HudInfoLine.TextAlignment.CENTER)
            )
        else
            mutableListOf()

    override fun onWrenchUse(wrench: TeslaWrench,
                             player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand,
                             facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (!this.getWorld().isRemote) {
            if (player.isSneaking) {
                val nbt = NBTTagCompound()
                nbt.setTag("tileentity", this.writeToNBT())
                val stack = ItemStack(Item.getItemFromBlock(this.getBlockType()), 1)
                stack.tagCompound = nbt
                val spawnPos = this.getPos()
                this.wasPickedUpInItemStack = true
                this.getWorld().setBlockToAir(spawnPos)
                this.spawnItem(stack, spawnPos, true)
                // this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true);
            } else if (this.getBlockType() is OrientedBlock<*>) {
                try {
                    return if (this.getBlockType().rotateBlock(this.getWorld(), this.getPos(), EnumFacing.UP))
                        EnumActionResult.SUCCESS
                    else
                        EnumActionResult.PASS
                } finally {
                    this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(), true)
                }
            }
        }

        return EnumActionResult.PASS
    }

    @SideOnly(Side.CLIENT)
    open fun getRenderers(): MutableList<TileEntitySpecialRenderer<TileEntity>> {
        return mutableListOf(HudInfoRenderer)
    }

    //endregion

    //#region gui / containers  methods

    override fun getContainer(id: Int, player: EntityPlayer): BasicTeslaContainer<*> {
        return BasicTeslaContainer(this, player)
    }

    override fun getGuiContainer(id: Int, player: EntityPlayer): BasicTeslaGuiContainer<*> {
        return BasicTeslaGuiContainer(id, this.getContainer(id, player), this)
    }

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = Lists.newArrayList<IGuiContainerPiece>()

        // side drawer
        if (this.canBePaused() && this.showPauseDrawerPiece) {
            pieces.add(object: SideDrawerPiece(0) {
                override val currentState: Int
                    get() = if (this@SidedTileEntity.isPaused()) 1 else 0

                override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
                    container.bindDefaultTexture()

                    GlStateManager.enableBlend()
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
                    GlStateManager.color(1f, 1f, 1f, 1f)
                    container.drawTexturedModalRect(box.left, box.top, 218, when(state % 2) {
                        0 -> 196
                        else -> 210
                    }, 14, 14)
                    GlStateManager.disableBlend()
                }

                override fun clicked() {
                    this@SidedTileEntity.togglePause()
                }
            })
        }

        pieces.add(MachineNameGuiPiece(this.getBlockType().unlocalizedName + ".name",
                7, 7, 162, 12))

        pieces.add(PlayerInventoryBackground(7, 101, 162, 54))
        if (this.showSideConfiguratorPiece) {
            val configurator = SideConfigurator(7, 101, 162, 54, this.sideConfig, this)
            pieces.add(configurator)
            pieces.add(SideConfigSelector(7, 81, 162, 18, this.sideConfig, configurator))
        }

        (0 until this.itemHandler.inventories)
            .map { this.itemHandler.getInventory(it) }
            .filterIsInstance<IGuiContainerPiecesProvider>()
            .map { it.getGuiContainerPieces(container) }
            .filter { it.size > 0 }
            .forEach { pieces.addAll(it) }

        val fluidPieces = this.fluidHandler.getGuiContainerPieces(container)
        if (fluidPieces.size > 0) {
            pieces.addAll(fluidPieces)
        }

        if (this.allowRedstoneControl && this.showRedstoneControlPiece) {
            pieces.add(RedstoneTogglePiece(this))
        }

        return pieces
    }

    protected open val showPauseDrawerPiece = true
    protected open val showSideConfiguratorPiece = true
    protected open val showRedstoneControlPiece = true

    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> =
        (0 until this.itemHandler.inventories)
            .map { this.itemHandler.getInventory(it) }
            .filterIsInstance<IContainerSlotsProvider>()
            .map { it.getSlots(container) }
            .filter { it.size > 0 }
            .fold(mutableListOf()) { slots, it -> slots.also { _ -> slots.addAll(it) } }

    //#endregion

    //#region redstone control  members

    override val allowRedstoneControl: Boolean
        get() = true

    override final var redstoneControl: IRedstoneControlledMachine.RedstoneControl
        get() = this.redstoneSetting
        set(value) {
            if (TeslaCoreLib.isClientSide && (this.world != null) && (this.pos != null)) {
                val message = this.setupSpecialNBTMessage("REDSTONE_CONTROL")
                message.setString("setting", value.name)
                this.sendToServer(message)
            } else {
                this.partialSync(SYNC_REDSTONE_CONTROL)
            }
            this.redstoneSetting = value
        }

    override final fun toggleRedstoneControl() {
        this.redstoneControl = this.redstoneSetting.getNext()
    }

    //#endregion

    //#region paused toggling   members

    open fun canBePaused() = true

    fun isPaused() = (this.canBePaused() && this.paused)

    fun togglePause() {
        if (!this.canBePaused()) {
            return
        }
        this.paused = !this.isPaused()

        @Suppress("SENSELESS_COMPARISON")
        if (this.getWorld() != null) {
            if (TeslaCoreLib.isClientSide) {
                val nbt = this.setupSpecialNBTMessage("PAUSE_MACHINE")
                nbt.setBoolean("pause", this.isPaused())
                this.sendToServer(nbt)
            } else {
                this.notifyNeighbours()
                this.partialSync(SYNC_IS_PAUSED)
            }
        }
    }

    //#endregion

    //#region item spawning     members

    fun onBlockBroken() {
        if (!this.wasPickedUpInItemStack) {
            this.processBlockBroken()
        }
    }

    protected fun processBlockBroken() {
        (0 until this.itemHandler.slots)
            .map { this.itemHandler.getStackInSlot(it) }
            .filterNot { it.isEmpty }
            .forEach { InventoryHelper.spawnItemStack(this.getWorld(), pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it) }
    }

    fun spawnItemFromFrontSide(stack: ItemStack): EntityItem? {
        val spawnPos = this.pos.offset(this.facing)
        return this.spawnItem(stack, spawnPos)
    }

    @JvmOverloads
    fun spawnItem(stack: ItemStack, spawnPos: BlockPos, forced: Boolean = false): EntityItem? {
        if (!(forced || TeslaCoreLib.config.allowMachinesToSpawnItems()) || stack.isEmpty || this.getWorld().isRemote) {
            return null
        }

        val entity = EntityItem(this.getWorld(),
                spawnPos.x + .5,
                spawnPos.y + .5,
                spawnPos.z + .5, stack)
        this.getWorld().spawnEntity(entity)
        return entity
    }

    //#endregion

    override final fun update() {
        if (!this.isPaused() && (!this.allowRedstoneControl || this.redstoneSetting.canRun {
            Math.max(this.world.getRedstonePower(this.pos, this.facing), this.world.isBlockIndirectlyGettingPowered(this.pos))
        })) {
            this.innerUpdate()
            this.processImmediateInventories()
        }

        super.update()
    }

    protected abstract fun innerUpdate()

    companion object {
        protected const val SYNC_SIDE_CONFIG = "side_config"
        protected const val SYNC_FLUIDS = "fluids"
        protected const val SYNC_FLUID_ITEMS = "fluidItems"
        protected const val SYNC_REDSTONE_CONTROL = "redstone"
        protected const val SYNC_IS_PAUSED = "paused"
    }
}
