package net.ndrei.teslacorelib.tileentities

import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagInt
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.energy.EnergySystemFactory
import net.ndrei.teslacorelib.gui.*
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.items.SpeedUpgradeTier1
import net.ndrei.teslacorelib.items.SpeedUpgradeTier2
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * Created by CF on 2017-06-27.
 */
abstract class ElectricMachine protected constructor(typeId: Int) : ElectricTileEntity(typeId), IWorkEnergyProvider {
    private var lastWorkTicks = 0
    private var workTick = 0

//    protected var outOfPower = false

    private lateinit var energyItems: ItemStackHandler

    private var workEnergy: EnergyStorage? = null

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.energyItems = object : ItemStackHandler(2) {
            override fun onContentsChanged(slot: Int) {
                // this@ElectricMachine.markDirty()
                this@ElectricMachine.partialSync(SYNC_ENERGY_ITEMS)
            }
        }
        super.addInventory(object : ColoredItemHandler(this.energyItems, EnumDyeColor.CYAN, "Energy Items", -10, BoundingRectangle(25, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack)
                    = (slot == 0) && (EnergySystemFactory.wrapItemStack(stack)?.tryTake() ?: false)

            override fun canExtractItem(slot: Int) = (slot > 0)

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = mutableListOf<Slot>()

                slots.add(FilteredSlot(this.itemHandlerForContainer, 0, 26, 26))
                slots.add(FilteredSlot(this.itemHandlerForContainer, 1, 26, 62))

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = mutableListOf<IGuiContainerPiece>()

                pieces.add(BasicRenderedGuiPiece(25, 25, 18, 54,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189))

                return pieces
            }
        })
        super.addInventoryToStorage(this.energyItems, SYNC_ENERGY_ITEMS)

        this.registerSyncTagPart(SYNC_WORK_ENERGY, Consumer {
            if (this.workEnergy == null) {
                this.resetWorkEnergyBuffer()
            }
            this.workEnergy!!.deserializeNBT(it)
        }, Supplier {
            if (this.workEnergy != null) {
                this.workEnergy!!.serializeNBT()
            } else {
                null
            }
        })

        this.registerSyncIntPart(SYNC_WORK_TICKS, Consumer { this.workTick = it.int }, Supplier { NBTTagInt(this.workTick) })
        this.registerSyncIntPart(SYNC_WORK_LAST_TICKS, Consumer { this.lastWorkTicks = it.int }, Supplier { NBTTagInt(this.lastWorkTicks) })
    }

    //#endregion
    //#region write/read/sync   methods

//    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
//        var compound = compound
//        compound = super.writeToNBT(compound)
//
////        compound.setInteger("tick_work", this.workTick)
////        compound.setInteger("tick_lastWork", this.lastWorkTicks)
////        compound.setBoolean("out_of_power", this.outOfPower)
//
////        if (this.workEnergy != null) {
////            compound.setTag(SYNC_WORK_ENERGY, this.workEnergy!!.serializeNBT())
////        }
//
//        return compound
//    }

//    override fun readFromNBT(compound: NBTTagCompound) {
//        super.readFromNBT(compound)
//
////        this.lastWorkTicks = compound.getInteger("tick_lastWork")
////        this.workTick = compound.getInteger("tick_work")
////        this.outOfPower = compound.getBoolean("out_of_power")
//
////        if (compound.hasKey(SYNC_WORK_ENERGY)) {
////            if (this.workEnergy == null) {
////                this.workEnergy = EnergyStorage(this.energyForWork.toLong(), this.energyForWorkRate.toLong(), 0)
////            }
////            this.workEnergy!!.deserializeNBT(compound.getCompoundTag(SYNC_WORK_ENERGY))
////        }
//    }

    //#endregion
    //#region gui / container   methods

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container).toMutableList()

        pieces.add(WorkEnergyIndicatorPiece(this, 7, 20))

        return pieces
    }

//    override val hudLines: List<HudInfoLine>
//        get() {
//            val list = super.hudLines.toMutableList()
//
//            if (this.outOfPower) {
//                list.add(HudInfoLine(Color.RED,
//                        Color(255, 0, 0, 42),
//                        "out of power")
//                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
//            }
//
//            return list.toList()
//        }

    //#endregion

    //region work              methods

    protected open val minimumWorkTicks: Int
        get() = 10

    protected open val energyForWork: Int
        get() = 600

    protected open val energyForWorkRate: Int
        get() = 20

    protected val energyForWorkRateMultiplier: Float
        get() {
            var ratio = 1.0f
            if (this.hasAddon(SpeedUpgradeTier1::class.java)) {
                ratio *= 1.5f
                if (this.hasAddon(SpeedUpgradeTier2::class.java)) {
                    ratio *= 1.5f
                }
            }
            return ratio
        }

    open fun supportsSpeedUpgrades(): Boolean {
        return true
    }

    open fun supportsEnergyUpgrades(): Boolean {
        return true
    }

    override val workEnergyCapacity: Long
        get() = if (this.workEnergy != null) this.workEnergy!!.capacity else 0

    override val workEnergyStored: Long
        get() = (if (this.workEnergy != null) this.workEnergy!!.stored else 0).toLong()

    override val workEnergyTick: Long
        get() = if (this.workEnergy != null) this.workEnergy!!.getEnergyInputRate() else 0

    private val finalEnergyForWork: Int
        get() = Math.round(
            this.addons
                .filter { it.isValid(this) }
                .fold(this.energyForWork.toFloat()) { energy, it -> energy * it.workEnergyMultiplier }
        )

    private fun resetWorkEnergyBuffer() {
        this.workEnergy = object : EnergyStorage(this.finalEnergyForWork.toLong(),
                Math.round(this.energyForWorkRate * this.energyForWorkRateMultiplier).toLong(),
            0) {
            override fun onChanged(old: Long, current: Long) {
                super.onChanged(old, current)
                this@ElectricMachine.partialSync(SYNC_WORK_ENERGY)
            }
        }
    }

    fun updateWorkEnergyRate() {
        if (this.workEnergy != null) {
            this.workEnergy!!.setEnergyInputRate(Math.round(this.energyForWorkRate * this.energyForWorkRateMultiplier).toLong())
        }
    }

    fun updateWorkEnergyCapacity() {
        if (this.workEnergy != null) {
            this.workEnergy!!.setCapacity(this.finalEnergyForWork.toLong())
        }
    }

    private fun setWorkTicks(value: Int) {
        if (this.workTick != value) {
            this.workTick = value
            // TODO: figure out if client cares about this or not
//            this.partialSync(SYNC_WORK_TICKS)
        }
    }

    private fun setLastWorkTicks(value: Int) {
        if (this.lastWorkTicks != value) {
            this.lastWorkTicks = value
            // TODO: figure out if client cares about this or not
//            this.partialSync(SYNC_WORK_LAST_TICKS)
        }
    }

    public override fun protectedUpdate() {
        if (this.workEnergy == null) {
            this.resetWorkEnergyBuffer()
        }

        if (!this.workEnergy!!.isFull) {
            val toTransfer = Math.min(
                    this.workEnergy!!.capacity - this.workEnergy!!.stored,
                    this.workEnergy!!.getEnergyInputRate())
            val transferred = this.energyStorage.takePower(toTransfer)
            if (transferred > 0) {
                this.workEnergy!!.givePower(transferred)
            }
        }

        this.setWorkTicks(Math.min(this.lastWorkTicks + 1, this.workTick + 1))
        if (this.workEnergy!!.isFull && this.workTick >= this.lastWorkTicks && !this.getWorld().isRemote) {
            val work = this.performWork()
            if (work > 0f) {
                val oldCapacity = this.workEnergy!!.capacity
                this.resetWorkEnergyBuffer()
                this.workEnergy!!.givePower(Math.round(oldCapacity * (1 - Math.max(0f, Math.min(1f, work)))).toLong())
            }
            // this.workTick = 0
            // this.lastWorkTicks = this.minimumWorkTicks
            this.setWorkTicks(0)
            this.setLastWorkTicks(this.minimumWorkTicks)
            // this.forceSync()
        }
    }

    protected abstract fun performWork(): Float

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        val stack = this.energyItems.getStackInSlot(0)
        val wrapper = EnergySystemFactory.wrapItemStack(stack)
        if (wrapper != null) {
            val power = wrapper.takePower(this.energyStorage.getEnergyInputRate(), true)
            if (power == 0L) {
                this.discardUsedEnergyItem()
            } else {
                val accepted = this.energyStorage.givePower(power, false)
                if (accepted > 0) {
                    wrapper.takePower(accepted, false)
                }
            }
        }
        else {
            this.discardUsedEnergyItem()
        }
    }

    private fun discardUsedEnergyItem() {
        val stack = this.energyItems.getStackInSlot(0)
        val remaining = this.energyItems.insertItem(1, stack, false)
        this.energyItems.setStackInSlot(0, remaining)
    }

    //endregion

    companion object {
        protected const val SYNC_ENERGY_ITEMS = "inv_energy_items"
        protected const val SYNC_WORK_ENERGY = "work_energy"
        protected const val SYNC_WORK_TICKS = "tick_work"
        protected const val SYNC_WORK_LAST_TICKS = "tick_lastWork"
    }
}
