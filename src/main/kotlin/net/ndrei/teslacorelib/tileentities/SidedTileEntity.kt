package net.ndrei.teslacorelib.tileentities

import com.google.common.collect.Lists
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.InventoryHelper
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
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
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.blocks.OrientedBlock
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
import net.ndrei.teslacorelib.netsync.ISimpleNBTMessageHandler
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage
import net.ndrei.teslacorelib.render.HudInfoLine
import net.ndrei.teslacorelib.render.HudInfoRenderer
import net.ndrei.teslacorelib.render.IHudInfoProvider
import net.ndrei.teslacorelib.utils.fillFrom
import net.ndrei.teslacorelib.utils.processInputInventory
import net.ndrei.teslacorelib.utils.withAlpha
import java.awt.Color

/**
 * Created by CF on 2017-06-27.
 */
abstract class SidedTileEntity protected constructor(protected val entityTypeId: Int)
    : TileEntity(), ITickable, IHudInfoProvider, ISimpleNBTMessageHandler, IGuiContainerProvider, ITeslaWrenchHandler, IRedstoneControlledMachine {
    private var syncTick = SYNC_ON_TICK

    protected val sideConfig: SidedItemHandlerConfig

    private val itemHandler: SidedItemHandler
    private val inventoryStorage: MutableList<SidedTileEntity.InventoryStorageInfo>

    protected val fluidHandler: SidedFluidHandler
    private var fluidItems: ItemStackHandler? = null
    protected var addonItems: ItemStackHandler? = null

    private var wasPickedUpInItemStack = false

    private var paused = false
    private var redstoneSetting = IRedstoneControlledMachine.RedstoneControl.AlwaysActive

    init {
        this.sideConfig = object : SidedItemHandlerConfig() {
            override fun updated() {
                this@SidedTileEntity.notifyNeighbours()
            }
        }
        this.itemHandler = SidedItemHandler(this.sideConfig)
        this.fluidHandler = SidedFluidHandler(this.sideConfig, this)
        this.inventoryStorage = mutableListOf()

        this.initializeInventories()
        this.ensureFluidItems()
    }

    private fun notifyNeighbours() {
        if (this.getWorld() != null) {
            this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getBlockType(),true)
        }
    }

    //region inventory         methods

    protected open fun initializeInventories() {
        this.createAddonsInventory()
    }

    protected fun addInventory(handler: IItemHandler?) {
        if (handler == null) {
            return
        }
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
        this.inventoryStorage.add(SidedTileEntity.InventoryStorageInfo(handler, storageKey))
    }

    private class InventoryStorageInfo internal constructor(internal val inventory: ItemStackHandler, internal val storageKey: String)

    open fun getInventoryLockState(color: EnumDyeColor): Boolean? {
        val inventory = (0..this.itemHandler.inventories-1)
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
        val inventory = (0..this.itemHandler.inventories-1)
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

    //endregion

    //region inventory addons  methods

    protected fun createAddonsInventory() {
        if (this.supportsAddons()) {
            this.addonItems = object : ItemStackHandler(4) {
                private val items = arrayOf<ItemStack?>(null, null, null, null)

                override fun onContentsChanged(slot: Int) {
                    this.testSlot(slot)
                }

                override fun onLoad() {
                    for (index in 0..this.slots - 1) {
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
                    this@SidedTileEntity.markDirty()
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
                    (0..3).mapTo(slots) { FilteredSlot(this.itemHandlerForContainer, it, 174, 8 + it * 18) }
                    return slots
                }

                override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                    val pieces = super.getGuiContainerPieces(container)

                    pieces.add(TiledRenderedGuiPiece(173, 7, 18, 18, 1, 4,
                            BasicTeslaGuiContainer.MACHINE_BACKGROUND, 144, 190, null))

                    return pieces
                }
            })
        }
    }

    protected open fun supportsAddons() = true

    protected fun <T : BaseAddon> getAddon(addonClass: Class<T>?): T? {
        if (this.addonItems != null && addonClass != null) {
            val addon = (0 until this.addonItems!!.slots)
                    .map { this.addonItems!!.getStackInSlot(it) }
                    .filterNot { it.isEmpty }
                    .map { it.item }
                    .filter { addonClass.isAssignableFrom(it.javaClass) }
                    .firstOrNull() ?: return null
            @Suppress("UNCHECKED_CAST")
            return addon as T
        }
        return null
    }

    protected fun <T : BaseAddon> getAddonStack(addonClass: Class<T>?): ItemStack {
        if (this.addonItems != null && addonClass != null) {
            val addon = (0 until this.addonItems!!.slots)
                    .map { this.addonItems!!.getStackInSlot(it) }
                    .filter { !it.isEmpty && addonClass.isAssignableFrom(it.item.javaClass) }
                    .firstOrNull()
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
            for (index in 0..this.addonItems!!.slots - 1) {
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
                this@SidedTileEntity.markDirty()
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
//
    protected fun addSimpleFluidTank(capacity: Int, name: String, color: EnumDyeColor, guiLeft: Int, guiTop: Int, tankType: FluidTankType,
                                     externalFillFilter: ((FluidStack) -> Boolean)? = null,
                                     externalDrainFilter: ((FluidStack) -> Boolean)? = null): IFluidTank {
        val tank = object: FluidTank(capacity) {
            override fun onContentsChanged() {
                this@SidedTileEntity.markDirty()
            }
        }

        this.addFluidTank(object: ColoredFluidHandler(tank, color, name, BoundingRectangle(guiLeft, guiTop, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
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
                }
                this.addInventory(object : ColoredItemHandler(this.fluidItems!!, color, "Fluid Containers", box) {
                    override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                        return slot == 0 && this@SidedTileEntity.acceptsFluidItem(stack)
                    }

                    override fun canExtractItem(slot: Int): Boolean {
                        return slot != 0
                    }

                    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                        val slots = mutableListOf<Slot>()

                        val box = this.boundingBox
                        if (!box.isEmpty) {
                            slots.add(FilteredSlot(this.itemHandlerForContainer, 0, box.left + 1, box.top + 1))
                            slots.add(FilteredSlot(this.itemHandlerForContainer, 1, box.left + 1, box.top + 1 + 36))
                        }

                        return slots
                    }

                    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                        val pieces = mutableListOf<IGuiContainerPiece>()

                        val box = this.boundingBox
                        if (!box.isEmpty) {
                            this@SidedTileEntity.addFluidItemsBackground(pieces, box)
                        }

                        return pieces
                    }
                })
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

    fun handleBucket(player: EntityPlayer, hand: EnumHand, side: EnumFacing): Boolean {
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

    //region storage & sync    methods

    val facing: EnumFacing
        get() {
            val state = this.getWorld().getBlockState(this.getPos())
            if (state.block is OrientedBlock<*>) {
                return state.getValue(OrientedBlock.FACING)
            }
            return EnumFacing.NORTH
        }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("addonItems") && this.addonItems != null) {
            this.addonItems!!.deserializeNBT(compound.getCompoundTag("addonItems"))
        }

        if (compound.hasKey("fluids")) {
            this.fluidHandler.deserializeNBT(compound.getCompoundTag("fluids"))
        }
        if (compound.hasKey("fluidItems") && this.fluidItems != null) {
            this.fluidItems!!.deserializeNBT(compound.getCompoundTag("fluidItems"))
        }

        this.syncTick = compound.getInteger("tick_sync")

        if (compound.hasKey("side_config", Constants.NBT.TAG_LIST)) {
            val list = compound.getTagList("side_config", Constants.NBT.TAG_COMPOUND)
            this.sideConfig.deserializeNBT(list)
        }

        if (!this.inventoryStorage.isEmpty()) {
            for (storage in this.inventoryStorage) {
                if (compound.hasKey(storage.storageKey, Constants.NBT.TAG_COMPOUND)) {
                    storage.inventory.deserializeNBT(compound.getCompoundTag(storage.storageKey))
                }
            }
        }

        if (compound.hasKey("redstone", Constants.NBT.TAG_STRING)) {
            this.redstoneSetting = IRedstoneControlledMachine.RedstoneControl.valueOf(compound.getString("redstone"))
        }

        this.paused = if (compound.hasKey("paused")) compound.getBoolean("paused") else false
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val nbt = super.writeToNBT(compound)

        nbt.setTag("fluids", this.fluidHandler.serializeNBT())
        if (this.fluidItems != null) {
            nbt.setTag("fluidItems", this.fluidItems!!.serializeNBT())
        }
        if (this.addonItems != null) {
            nbt.setTag("addonItems", this.addonItems!!.serializeNBT())
        }

        nbt.setInteger("tick_sync", this.syncTick)

        nbt.setTag("side_config", this.sideConfig.serializeNBT())

        if (!this.inventoryStorage.isEmpty()) {
            for (storage in this.inventoryStorage) {
                nbt.setTag(storage.storageKey, storage.inventory.serializeNBT())
            }
        }

        if (this.canBePaused())
            nbt.setBoolean("paused", this.paused)

        nbt.setString("redstone", this.redstoneSetting.name)

        return nbt
    }

    private fun writeToNBT(): NBTTagCompound {
        val compound = this.setupSpecialNBTMessage()
        return this.writeToNBT(compound)
    }

    fun setupSpecialNBTMessage(messageType: String? = null): NBTTagCompound {
        val compound = NBTTagCompound()
        compound.setInteger("__tetId", this.entityTypeId)
        if ((messageType != null) && messageType.isNotEmpty()) {
            compound.setString("__messageType", messageType)
        }
        return compound
    }

    override fun handleServerMessage(message: SimpleNBTMessage): SimpleNBTMessage? {
        val compound = message.compound
        if (compound != null) {
            val tetId = compound.getInteger("__tetId")
            if (tetId == this.entityTypeId) {
                if (compound.hasKey("__messageType", Constants.NBT.TAG_STRING)) {
                    val messageType = compound.getString("__messageType")
                    return this.processServerMessage(messageType, compound)
                } else /*if (this.getWorld().isRemote)*/ {
                    this.processServerMessage(compound)
                }
            } else {
                TeslaCoreLib.logger.info("Unknown message for __tetId: " + tetId + " : " + compound.toString())
            }
        }
        return null
    }

    override fun handleClientMessage(player: EntityPlayerMP?, message: SimpleNBTMessage): SimpleNBTMessage? {
        val compound = message.compound
        if (compound != null) {
            val tetId = compound.getInteger("__tetId")
            if (tetId == this.entityTypeId) {
                if (compound.hasKey("__messageType", Constants.NBT.TAG_STRING)) {
                    val messageType = compound.getString("__messageType")
                    return this.processClientMessage(messageType, player, compound)
                }
            } else {
                TeslaCoreLib.logger.info("Unknown message for __tetId: " + tetId + " : " + compound.toString())
            }
        }
        return null
    }

    protected fun processServerMessage(compound: NBTTagCompound) {
        this.readFromNBT(compound)
    }

    protected open fun processServerMessage(messageType: String, compound: NBTTagCompound): SimpleNBTMessage? {
        return null
    }

    protected open fun processClientMessage(messageType: String?, player: EntityPlayerMP?, compound: NBTTagCompound): SimpleNBTMessage? {
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
                                    if ((it is ITypedFluidTank) && (it is IFluidTankWrapper) && (it.tankType == FluidTankType.INPUT))
                                        it.innerTank
                                    else
                                        it
                                }
                        if (tank != null) {
                            player.inventory.itemStack = stack.fillFrom(tank)
                            player.connection.sendPacket(SPacketSetSlot(-1, 0, player.inventory.itemStack))
                        }
                    }
                }
                else {
                    TeslaCoreLib.logger.error("Cannot fill a tank without a player!")
                }
                return null
            }
        }
        return this.processClientMessage(messageType, compound)
    }

    protected open fun processClientMessage(messageType: String?, compound: NBTTagCompound): SimpleNBTMessage? {
        if (messageType != null && messageType == "TOGGLE_SIDE") {
            val color = EnumDyeColor.byMetadata(compound.getInteger("color"))
            val facing = EnumFacing.getFront(compound.getInteger("side"))
            this.sideConfig.toggleSide(color, facing)
            this.markDirty()
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

        return null
    }

    fun sendToServer(compound: NBTTagCompound) {
        TeslaCoreLib.network.sendToServer(SimpleNBTMessage(this, compound))
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
            if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                val slots = this.itemHandler.getSlotsForFace(oriented)
                return slots.isNotEmpty()
            } else if (this.fluidHandler.hasCapability(capability, oriented)) {
                return true
            }
        }

        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        val oriented = this.orientFacing(facing)

        if (capability === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemHandler.getSideWrapper(oriented) as T
        } else if (capability === TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER) {
            return this as T
        } else if (capability === TeslaCoreCapabilities.CAPABILITY_WRENCH) {
            return this as T
        }

        val c = this.fluidHandler.getCapability(capability, oriented)
        if (c != null) {
            return c
        }

        return super.getCapability(capability, facing)
    }

    override fun getHudLines(face: EnumFacing?): List<HudInfoLine> {
        return listOf(*(if ((face != null) && Minecraft.getMinecraft().player.isSneaking) {
            arrayOf(HudInfoLine(Color.LIGHT_GRAY, Color.LIGHT_GRAY.withAlpha(.24f), "Side: ${this.getSideDirection(face).toUpperCase()} (${face.toString().capitalize()})"))
        } else arrayOf<HudInfoLine>()), *this.hudLines.toTypedArray())
    }

    protected fun getSideDirection(face: EnumFacing)
            = when (this.facing) {
        face -> "front"
        face.opposite -> "back"
        face.rotateY() -> "right"
        face.rotateYCCW() -> "left"
        else -> "[error]"
    }

    protected open val hudLines: List<HudInfoLine>
        get() = if (this.isPaused())
            listOf(
                    HudInfoLine(Color.RED, Color.RED.withAlpha(.24f), "PAUSED").setTextAlignment(HudInfoLine.TextAlignment.CENTER)
            )
        else
            listOf()

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
                this.spawnItem(stack, spawnPos)
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
        if (this.canBePaused()) {
            pieces.add(object: SideDrawerPiece(0) {
                override val currentState: Int
                    get() = if (this@SidedTileEntity.isPaused()) 1 else 0

                override fun renderState(container: BasicTeslaGuiContainer<*>, state: Int, box: BoundingRectangle) {
                    container.bindDefaultTexture()
                    container.drawTexturedModalRect(box.left, box.top, 218, when(state % 2) {
                        0 -> 196
                        else -> 210
                    }, 14, 14)
                }

                override fun clicked() {
                    this@SidedTileEntity.togglePause()
                }
            })
        }

        pieces.add(MachineNameGuiPiece(this.getBlockType().unlocalizedName + ".name",
                7, 7, 162, 12))

        pieces.add(PlayerInventoryBackground(7, 101, 162, 54))
        val configurator = SideConfigurator(7, 101, 162, 54, this.sideConfig, this)
        pieces.add(configurator)
        pieces.add(SideConfigSelector(7, 81, 162, 18, this.sideConfig, configurator))

        for (i in 0..this.itemHandler.inventories - 1) {
            val handler = this.itemHandler.getInventory(i)
            if (handler is IGuiContainerPiecesProvider) {
                val childPieces = (handler as IGuiContainerPiecesProvider).getGuiContainerPieces(container)
                if (childPieces.size > 0) {
                    pieces.addAll(childPieces)
                }
            }
        }

        val fluidPieces = this.fluidHandler.getGuiContainerPieces(container)
        if (fluidPieces.size > 0) {
            pieces.addAll(fluidPieces)
        }

        if (this.allowRedstoneControl) {
            pieces.add(RedstoneTogglePiece(this))
        }

        return pieces
    }

    override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
        val slots = Lists.newArrayList<Slot>()

        for (i in 0..this.itemHandler!!.inventories - 1) {
            val handler = this.itemHandler.getInventory(i)
            if (handler is IContainerSlotsProvider) {
                val childSlots = (handler as IContainerSlotsProvider).getSlots(container)
                if (childSlots != null && childSlots.size > 0) {
                    slots.addAll(childSlots)
                }
            }
        }

        return slots
    }

    //#endregion

    //#region redstone control  members

    override val allowRedstoneControl: Boolean
        get() = true
    override final val redstoneControl: IRedstoneControlledMachine.RedstoneControl
        get() = this.redstoneSetting

    override final fun toggleRedstoneControl() {
        val new = this.redstoneSetting.getNext()
        if (TeslaCoreLib.isClientSide) {
            val message = this.setupSpecialNBTMessage("REDSTONE_CONTROL")
            message.setString("setting", new.name)
            this.sendToServer(message)
        }
        this.redstoneSetting = new
    }

    //#endregion

    open fun canBePaused() = true

    fun isPaused() = (this.canBePaused() && this.paused)

    fun togglePause() {
        if (!this.canBePaused()) {
            return
        }
        this.paused = !this.isPaused()

        if (TeslaCoreLib.isClientSide) {
            val nbt = this.setupSpecialNBTMessage("PAUSE_MACHINE")
            nbt.setBoolean("pause", this.isPaused())
            this.sendToServer(nbt)
        }
        else {
            this.notifyNeighbours()
        }
    }

    override final fun update() {
        if (!this.isPaused() && (!this.allowRedstoneControl || this.redstoneSetting.canRun { this.world.getRedstonePower(this.pos, this.facing) })) {
            this.innerUpdate()
            this.processImmediateInventories()
        }

        if (!this.getWorld().isRemote) {
            this.syncTick++
            if (this.syncTick >= SYNC_ON_TICK) {
                TeslaCoreLib.network.send(SimpleNBTMessage(this, this.writeToNBT()))
                this.syncTick = 0
            }
        }
    }

    protected abstract fun innerUpdate()

    protected fun forceSync() {
        if (this.getWorld() != null && !this.getWorld().isRemote) {
            this.syncTick = SYNC_ON_TICK
        }
    }

    protected open fun processImmediateInventories() {
        if (this.fluidItems != null) {
            this.processFluidItems(this.fluidItems!!)
        }
    }

    fun onBlockBroken() {
        if (!this.wasPickedUpInItemStack) {
            this.processBlockBroken()
        }
    }

    protected fun processBlockBroken() {
        for (i in 0 until this.itemHandler.slots) {
            val stack = this.itemHandler.getStackInSlot(i)
            if (!stack.isEmpty) {
                InventoryHelper.spawnItemStack(this.getWorld(), pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }
    }

    fun spawnItemFromFrontSide(stack: ItemStack): EntityItem? {
        val spawnPos = this.pos.offset(this.facing)
        return this.spawnItem(stack, spawnPos)
    }

    fun spawnItem(stack: ItemStack, spawnPos: BlockPos): EntityItem? {
        if (!TeslaCoreLib.config.allowMachinesToSpawnItems() || stack.isEmpty || this.getWorld().isRemote) {
            return null
        }

        val entity = EntityItem(this.getWorld(),
                spawnPos.x + .5,
                spawnPos.y + .5,
                spawnPos.z + .5, stack)
        this.getWorld().spawnEntity(entity)
        return entity
    }

    companion object {
        private const val SYNC_ON_TICK = 20
    }
}
