package net.ndrei.teslacorelib.tileentities

import cofh.redstoneflux.api.IEnergyProvider
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.compatibility.RFPowerProxy
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.energy.EnergySystemFactory
import net.ndrei.teslacorelib.energy.IGenericEnergyStorage
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.EnergyStorage

/**
 * Created by CF on 2017-06-27.
 */
@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = RFPowerProxy.MODID, striprefs = true)
abstract class ElectricGenerator protected constructor(typeId: Int) : ElectricTileEntity(typeId), IEnergyProvider {
    private var generatedPower: EnergyStorage? = null
    private var chargePadItems: ItemStackHandler? = null

    override fun initializeInventories() {
        super.initializeInventories()

        this.chargePadItems = object : ItemStackHandler(2) {
            override fun onContentsChanged(slot: Int) {
                this@ElectricGenerator.markDirty()
            }

            override fun getSlotLimit(slot: Int): Int {
                return 1
            }
        }
        super.addInventory(object : ColoredItemHandler(this.chargePadItems!!, EnumDyeColor.BROWN, "Charge Pad", -10, BoundingRectangle(34, 34, 18, 36)) {
            override fun canInsertItem(slot: Int, stack: ItemStack)
                    = EnergySystemFactory.wrapItemStack(stack)?.canGive ?: false

            override fun canExtractItem(slot: Int): Boolean {
                val wrapper = EnergySystemFactory.wrapItemStack(this.getStackInSlot(slot))
                if (wrapper != null) {
                    return (wrapper.givePower(1L, true) > 0L)
                }
                return true
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = super.getSlots(container)

                slots.add(FilteredSlot(this.itemHandlerForContainer, 0, 35, 35))
                slots.add(FilteredSlot(this.itemHandlerForContainer, 1, 35, 53))

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = super.getGuiContainerPieces(container)

                pieces.add(BasicRenderedGuiPiece(25, 26, 27, 52,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 206, 4))

                return pieces
            }
        })
        super.addInventoryToStorage(this.chargePadItems!!, "inv_charge_pad")
    }

    //region energy            methods

    override val maxEnergy: Long
        get() = 100000

    override val energyInputRate: Long
        get() = 0

    override val energyOutputRate: Long
        get() = 80

    protected open val energyFillRate: Long
        get() = 160

    val generatedPowerCapacity: Long
        get() = if (this.generatedPower == null) 0 else this.generatedPower!!.capacity

    val generatedPowerStored: Long
        get() = if (this.generatedPower == null) 0 else this.generatedPower!!.stored

    val generatedPowerReleaseRate: Long
        get() = if (this.generatedPower == null) 0 else this.generatedPower!!.getEnergyOutputRate()

    //endregion

    //#region work              methods

    protected abstract fun consumeFuel(): Long
    protected open fun fuelConsumed() {}

    protected val isGeneratedPowerLostIfFull: Boolean
        get() = true

    public override fun protectedUpdate() {
        if (this.generatedPower != null && !this.generatedPower!!.isEmpty) {
            val power = this.generatedPower!!.takePower(this.generatedPower!!.getEnergyOutputRate(), !this.isGeneratedPowerLostIfFull)
            val consumed = this.energyStorage.givePower(power)
            if (consumed > 0 && this.isGeneratedPowerLostIfFull) {
                this.generatedPower!!.takePower(consumed)
            }

            // TeslaCoreLib.logger.info("generated power: " + this.generatedPower.getStoredPower() + " / " + this.generatedPower.getCapacity());
            if (this.generatedPower!!.isEmpty) {
                this.fuelConsumed()
            }
        }

        if ((this.generatedPower == null || this.generatedPower!!.isEmpty) && !this.energyStorage.isFull && !this.getWorld().isRemote) {
            this.generatedPower = null

            val power = this.consumeFuel()
            if (power > 0) {
                this.generatedPower = EnergyStorage(power, 0, this.energyFillRate)
                this.generatedPower!!.givePower(power)
                this.forceSync()
            }
        }

        //#region distribute power

        // TODO: research if this should be done only on server side or not
        if (!this.energyStorage.isEmpty) {
            val consumers = mutableListOf<IGenericEnergyStorage>()

            val powerSides = this.sideConfig.getSidesForColor(this.energyStorage.color!!)
            if (powerSides.isNotEmpty()) {
                val pos = this.getPos()
                val facing = this.facing
                for (side in powerSides) {
                    var oriented: EnumFacing = this.orientFacing(side)
                    if ((oriented != EnumFacing.DOWN) && oriented != EnumFacing.UP && (facing == EnumFacing.EAST || facing == EnumFacing.WEST)) {
                        oriented = oriented.opposite
                    }

                    val entity = this.getWorld().getTileEntity(pos.offset(oriented)) ?: continue
                    val wrapper = EnergySystemFactory.wrapTileEntity(entity, oriented.opposite) ?: continue
                    consumers.add(wrapper)
                }
            }

            if (consumers.size > 0) {
                var total = this.energyStorage.getEnergyOutputRate()
                total = this.energyStorage.takePower(total, true)
                var totalConsumed: Long = 0
                var consumerCount = consumers.size
                for (consumer in consumers) {
                    val perConsumer = total / consumerCount
                    consumerCount--
                    if (perConsumer > 0) {
                        val consumed = consumer.givePower(perConsumer, false)
                        if (consumed > 0) {
                            totalConsumed += consumed
                        }
                    }
                }

                if (totalConsumed > 0) {
                    this.energyStorage.takePower(totalConsumed)
                }
            }
        }

        //#endregion
    }

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        for (index in 0..1) {
            val stack = this.chargePadItems!!.getStackInSlot(index)
            if (ItemStackUtil.isEmpty(stack)) {
                continue
            }

            val available = this.energyStorage.takePower(this.energyStorage.getEnergyOutputRate(), true)
            if (available == 0L) {
                break
            }

            val wrapper = EnergySystemFactory.wrapItemStack(stack)
            if (wrapper != null) {
                val consumed = wrapper.givePower(available, false)
                if (consumed > 0) {
                    this.energyStorage.takePower(consumed, false)
                }
            }
        }
    }

    //#endregion
    //#region write/read/sync   methods

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        if (this.generatedPower != null) {
            compound.setTag("generated_energy", this.generatedPower!!.serializeNBT())
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("generated_energy")) {
            if (this.generatedPower == null) {
                this.generatedPower = EnergyStorage(0, 0, 0)
            }
            this.generatedPower!!.deserializeNBT(compound.getCompoundTag("generated_energy"))
        }
    }

    //#endregion

    //#region RF Power Support

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun getMaxEnergyStored(from: EnumFacing?): Int {
        if (this.energyStorage.isSideAllowed(this.orientFacing(from))) {
            return this.energyStorage.capacity.toInt()
        }
        return 0
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun getEnergyStored(from: EnumFacing?): Int {
        if (this.energyStorage.isSideAllowed(this.orientFacing(from))) {
            return this.energyStorage.stored.toInt()
        }
        return 0
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun extractEnergy(from: EnumFacing?, maxExtract: Int, simulate: Boolean): Int {
        if (this.energyStorage.isSideAllowed(this.orientFacing(from))) {
            return this.energyStorage.takePower(maxExtract.toLong(), simulate).toInt()
        }
        return 0
    }

    @Optional.Method(modid = RFPowerProxy.MODID)
    override fun canConnectEnergy(from: EnumFacing?)
            = this.energyStorage.isSideAllowed(this.orientFacing(from))

    //#endregion
}
