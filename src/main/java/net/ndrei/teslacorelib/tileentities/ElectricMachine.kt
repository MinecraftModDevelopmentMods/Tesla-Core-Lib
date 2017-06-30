package net.ndrei.teslacorelib.tileentities

import net.darkhax.tesla.capability.TeslaCapabilities
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.*
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.items.SpeedUpgradeTier1
import net.ndrei.teslacorelib.items.SpeedUpgradeTier2
import java.awt.Color

/**
 * Created by CF on 2017-06-27.
 */
abstract class ElectricMachine protected constructor(typeId: Int) : ElectricTileEntity(typeId), IWorkEnergyProvider {
    private var lastWorkTicks = 0
    private var workTick = 0

    protected var outOfPower = false

    private var energyItems: ItemStackHandler? = null

    private var workEnergy: EnergyStorage? = null

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.energyItems = object : ItemStackHandler(2) {
            override fun onContentsChanged(slot: Int) {
                this@ElectricMachine.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.energyItems!!, EnumDyeColor.CYAN, "Energy Items", -10, BoundingRectangle(25, 25, 18, 54)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return this@ElectricMachine.canInsertEnergyItem(slot, stack)
            }

            override fun canExtractItem(slot: Int): Boolean {
                return slot != 0
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = super.getSlots(container)

                slots.add(FilteredSlot(this.itemHandlerForContainer, 0, 26, 26))
                slots.add(FilteredSlot(this.itemHandlerForContainer, 1, 26, 62))

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = super.getGuiContainerPieces(container)

                pieces.add(BasicRenderedGuiPiece(25, 25, 18, 54,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189))

                return pieces
            }
        })
        super.addInventoryToStorage(this.energyItems!!, "inv_energy_items")
    }

    private fun canInsertEnergyItem(slot: Int, stack: ItemStack): Boolean {
        if (slot != 0) {
            return false
        }

        val tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)
        if (tesla != null) {
            val holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
            if (holder == null || holder.storedPower > 0) {
                return true
            }
        }
        return false
    }

    //#endregion
    //#region write/read/sync   methods

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        compound.setInteger("tick_work", this.workTick)
        compound.setInteger("tick_lastWork", this.lastWorkTicks)
        compound.setBoolean("out_of_power", this.outOfPower)

        if (this.workEnergy != null) {
            compound.setTag("work_energy", this.workEnergy!!.serializeNBT())
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        this.lastWorkTicks = compound.getInteger("tick_lastWork")
        this.workTick = compound.getInteger("tick_work")
        this.outOfPower = compound.getBoolean("out_of_power")

        if (compound.hasKey("work_energy")) {
            if (this.workEnergy == null) {
                this.workEnergy = EnergyStorage(this.energyForWork.toLong(), this.energyForWorkRate.toLong(), 0)
            }
            this.workEnergy!!.deserializeNBT(compound.getCompoundTag("work_energy"))
        }
    }

    //#endregion
    //#region gui / container   methods

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
        val pieces = super.getGuiContainerPieces(container).toMutableList()

        pieces.add(WorkEnergyIndicatorPiece(this, 7, 20))

        return pieces
    }

    override val hudLines: List<HudInfoLine>
        get() {
            val list = super.hudLines.toMutableList()

            if (this.outOfPower) {
                list.add(HudInfoLine(Color.RED,
                        Color(255, 0, 0, 42),
                        "out of power")
                        .setTextAlignment(HudInfoLine.TextAlignment.CENTER))
            }

            return list.toList()
        }

    //#endregion

    //region work              methods

    protected val minimumWorkTicks: Int
        get() = 10

    protected val energyForWork: Int
        get() = 600

    protected val energyForWorkRate: Int
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

    fun supportsSpeedUpgrades(): Boolean {
        return true
    }

    fun supportsEnergyUpgrades(): Boolean {
        return true
    }

    override val workEnergyCapacity: Long
        get() = if (this.workEnergy != null) this.workEnergy!!.capacity else 0

    override val workEnergyStored: Long
        get() = (if (this.workEnergy != null) this.workEnergy!!.energyStored else 0).toLong()

    override val workEnergyTick: Long
        get() = if (this.workEnergy != null) this.workEnergy!!.getInputRate() else 0

    private val finalEnergyForWork: Int
        get() {
            var energy = this.energyForWork.toFloat()
            for (addon in this.addons) {
                if (!addon.isValid(this)) {
                    continue
                }

                energy *= addon.workEnergyMultiplier
            }
            return Math.round(energy)
        }

    protected fun resetWorkEnergyBuffer() {
        this.workEnergy = EnergyStorage(this.finalEnergyForWork.toLong(),
                Math.round(this.energyForWorkRate * this.energyForWorkRateMultiplier).toLong(),
                0)
    }

    fun updateWorkEnergyRate() {
        if (this.workEnergy != null) {
            this.workEnergy!!.setInputRate(Math.round(this.energyForWorkRate * this.energyForWorkRateMultiplier).toLong())
        }
    }

    fun updateWorkEnergyCapacity() {
        if (this.workEnergy != null) {
            this.workEnergy!!.capacity = this.finalEnergyForWork.toLong()
        }
    }

    public override fun protectedUpdate() {
        if (this.workEnergy == null) {
            this.resetWorkEnergyBuffer()
        }

        if (!this.workEnergy!!.isFull) {
            val toTransfer = Math.min(
                    this.workEnergy!!.capacity - this.workEnergy!!.storedPower,
                    this.workEnergy!!.getInputRate())
            val transferred = this.energyStorage.takePower(toTransfer)
            this.workEnergy!!.givePower(transferred)
        }

        this.workTick = Math.min(this.lastWorkTicks + 1, this.workTick + 1)
        if (this.workEnergy!!.isFull && this.workTick >= this.lastWorkTicks && !this.getWorld().isRemote) {
            val work = this.performWork()
            val oldCapacity = this.workEnergy!!.capacity
            this.resetWorkEnergyBuffer()
            this.workEnergy!!.givePower(Math.round(oldCapacity * (1 - Math.max(0f, Math.min(1f, work)))).toLong())

            this.workTick = 0
            this.lastWorkTicks = this.minimumWorkTicks
            this.forceSync()
        }
    }

    protected abstract fun performWork(): Float

    override fun processImmediateInventories() {
        super.processImmediateInventories()

        if (this.energyItems != null) {
            val stack = this.energyItems!!.getStackInSlot(0)
            if (!ItemStackUtil.isEmpty(stack)) {
                val producer = stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)
                if (producer != null) {
                    val power = producer.takePower(this.energyStorage.getInputRate(), true)
                    if (power == 0L) {
                        this.discardUsedEnergyItem()
                    } else {
                        val accepted = this.energyStorage.givePower(power, false)
                        producer.takePower(accepted, false)
                    }
                } else {
                    this.discardUsedEnergyItem()
                }
            }
        }
    }

    private fun discardUsedEnergyItem() {
        val stack = this.energyItems!!.getStackInSlot(0)
        val remaining = this.energyItems!!.insertItem(1, stack, false)
        this.energyItems!!.setStackInSlot(0, remaining)
    }

    //endregion
}
