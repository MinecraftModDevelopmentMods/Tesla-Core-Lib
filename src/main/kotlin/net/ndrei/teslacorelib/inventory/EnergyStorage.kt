package net.ndrei.teslacorelib.inventory

import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig
import net.ndrei.teslacorelib.energy.EnergySystemFactory
import net.ndrei.teslacorelib.energy.GenericEnergyStorage

/**
 * Created by CF on 2017-06-28.
 */
open class EnergyStorage(maxStoredEnergy: Long, inputRate: Long, outputRate: Long)
    : GenericEnergyStorage(maxStoredEnergy, inputRate, outputRate), /* ITeslaConsumer, ITeslaHolder, ITeslaProducer, IStrictEnergyAcceptor, IEnergyStorage,*/ INBTSerializable<NBTTagCompound>, ICapabilityProvider, IEnergyStatistics {

    var color: EnumDyeColor? = null
        private set

    private var sidedConfig: ISidedItemHandlerConfig? = null

    private var statStored: Long = 0
    override final var averageEnergyPerTick: Long = 0
        private set
    override final var lastTickEnergy: Long = 0
        private set
    private val statTicks = mutableListOf<Long>()

    //region forge energy

//    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
//        return this.givePower(maxReceive.toLong(), simulate).toInt()
//    }
//
//    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
//        return this.takePower(maxExtract.toLong(), simulate).toInt()
//    }
//
//    override fun getEnergyStored(): Int {
//        return Math.min(Integer.MAX_VALUE.toLong(), this.storedPower).toInt()
//    }
//
//    override fun getMaxEnergyStored(): Int {
//        return Math.min(Integer.MAX_VALUE.toLong(), this.getCapacity()).toInt()
//    }
//
//    override fun canExtract(): Boolean {
//        return this.getOutputRate() > 0
//    }
//
//    override fun canReceive(): Boolean {
//        return this.getInputRate() > 0
//    }

    //endregion
    //region IStrictEnergyAcceptor

//    @Optional.Method(modid = "Mekanism")
//    override fun getEnergy(): Double {
//        return this.energyStored.toDouble()
//    }
//
//    @Optional.Method(modid = "Mekanism")
//    override fun setEnergy(energy: Double) {
//        // TODO: ??
//    }
//
//    @Optional.Method(modid = "Mekanism")
//    override fun getMaxEnergy(): Double {
//        return this.maxEnergyStored.toDouble()
//    }
//
//    @Optional.Method(modid = "Mekanism")
//    override fun transferEnergyToAcceptor(side: EnumFacing, amount: Double): Double {
//        var tesla = Math.round(amount.toFloat() * .4f)
//        tesla = this.receiveEnergy(tesla, false)
//        return tesla / .4
//    }
//
//    @Optional.Method(modid = "Mekanism")
//    override fun canReceiveEnergy(side: EnumFacing): Boolean {
//        return this.canReceive()
//    }

    //endregion
    //#region tesla energy

//    override fun getStoredPower(): Long {
//        return this.stored
//    }
//
//    override fun givePower(tesla: Long, simulated: Boolean): Long {
//        return this.givePower(tesla, simulated, false)
//    }
//
//    private fun givePower(tesla: Long, simulated: Boolean, forced: Boolean): Long {
//        val acceptedTesla = if (forced)
//            Math.min(this.getCapacity() - this.stored, tesla)
//        else
//            Math.min(this.getCapacity() - this.stored, Math.min(this.getInputRate(), tesla))
//
//        if (!simulated) {
//            this.stored += acceptedTesla
//            this.onChanged()
//        }
//
//        return acceptedTesla
//    }
//
//    override fun takePower(tesla: Long, simulated: Boolean): Long {
//        return this.takePower(tesla, simulated, false)
//    }
//
//    private fun takePower(tesla: Long, simulated: Boolean, forced: Boolean): Long {
//        val removedPower = if (forced)
//            Math.min(this.stored, tesla)
//        else
//            Math.min(this.stored, Math.min(this.getOutputRate(), tesla))
//
//        if (!simulated) {
//            this.stored -= removedPower
//            this.onChanged()
//        }
//
//        return removedPower
//    }
//
//    override fun getCapacity(): Long {
//        return this.capacity
//    }

    //#endregion

    //region util method

    fun workPerformed(jobEnergy: Long): Long {
        return this.workPerformed(jobEnergy, 1.0f)
    }

    fun workPerformed(jobEnergy: Long, jobPercent: Float): Long {
        val energy = Math.round(jobEnergy.toDouble() * Math.max(0f, Math.min(1f, jobPercent)))
        return this.takePower(energy)
    }

    fun givePower(energy: Long): Long {
        return this.givePower(energy, false, true)
    }

    fun takePower(energy: Long): Long {
        return this.takePower(energy, false, true)
    }

    val isFull: Boolean
        get() = this.capacity == this.stored

    val isEmpty: Boolean
        get() = this.stored == 0L

    /**
     * Sets the capacity of the the container. If the existing stored power is more than the
     * new capacity, the stored power will be decreased to match the new capacity.

     * @param capacity The new capacity for the container.
     * *
     * @return The instance of the container being updated.
     */
    fun setCapacity(capacity: Long): EnergyStorage {
        val stored = this.stored
        this.capacity = capacity

        if (this.stored > capacity)
            this.stored = capacity

        if (stored != this.stored) {
            this.onChanged(stored, this.stored)
        }
        return this
    }

    /**
     * Gets the maximum amount of Tesla power that can be accepted by the container.

     * @return The amount of Tesla power that can be accepted at any time.
     */
    fun getEnergyInputRate(): Long {
        return this.inputRate
    }

    /**
     * Sets the maximum amount of Tesla power that can be accepted by the container.

     * @param rate The amount of Tesla power to accept at a time.
     * *
     * @return The instance of the container being updated.
     */
    fun setEnergyInputRate(rate: Long): EnergyStorage {
        this.inputRate = rate
        return this
    }

    /**
     * Gets the maximum amount of Tesla power that can be pulled from the container.

     * @return The amount of Tesla power that can be extracted at any time.
     */
    fun getEnergyOutputRate(): Long {
        return this.outputRate
    }

    /**
     * Sets the maximum amount of Tesla power that can be pulled from the container.

     * @param rate The amount of Tesla power that can be extracted.
     * *
     * @return The instance of the container being updated.
     */
    fun setEnergyOutputRate(rate: Long): EnergyStorage {
        this.outputRate = rate
        return this
    }

    /**
     * Sets both the input and output rates of the container at the same time. Both rates will
     * be the same.

     * @param rate The input/output rate for the Tesla container.
     * *
     * @return The instance of the container being updated.
     */
    fun setEnergyTransferRate(rate: Long): EnergyStorage {
        this.setEnergyInputRate(rate)
        this.setEnergyOutputRate(rate)
        return this
    }

    //endregion

    //#region nbt storage

    override fun serializeNBT(): NBTTagCompound {
        val dataTag = NBTTagCompound()
        dataTag.setLong("TeslaPower", this.stored)
        dataTag.setLong("TeslaCapacity", this.capacity)
        dataTag.setLong("TeslaInput", this.inputRate)
        dataTag.setLong("TeslaOutput", this.outputRate)

        return dataTag
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val originalStored = this.stored
        this.stored = nbt.getLong("TeslaPower")

        if (nbt.hasKey("TeslaCapacity"))
            this.capacity = nbt.getLong("TeslaCapacity")

        if (nbt.hasKey("TeslaInput"))
            this.inputRate = nbt.getLong("TeslaInput")

        if (nbt.hasKey("TeslaOutput"))
            this.outputRate = nbt.getLong("TeslaOutput")

        if (this.stored > this.capacity)
            this.stored = this.capacity

        if (this.stored != originalStored) {
            this.onChanged(originalStored, this.stored)
        }
    }

    //#endregion

    fun isSideAllowed(facing: EnumFacing?)
        = ((this.sidedConfig != null) && (this.color != null) && (facing != null) && this.sidedConfig!!.isSideSet(this.color!!, facing))

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (this.isSideAllowed(facing)) {
            return EnergySystemFactory.isCapabilitySupported(capability)
        }

        return false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (this.isSideAllowed(facing)) {
            return EnergySystemFactory.wrapCapability(capability, this)
        }
        return null
    }

    fun setSidedConfig(color: EnumDyeColor, sidedConfig: ISidedItemHandlerConfig, highlight: BoundingRectangle) {
        if (this.sidedConfig === sidedConfig) {
            return
        }

        this.sidedConfig = sidedConfig
        this.color = color
        if ((this.sidedConfig != null) && (this.color != null)) {
            this.sidedConfig!!.addColoredInfo("Energy", this.color!!, highlight, -20)
        }
    }

    fun processStatistics() {
        this.lastTickEnergy = this.stored - this.statStored
        this.statStored = this.stored

        this.statTicks.add(this.lastTickEnergy)
        while (this.statTicks.size > 10) {
            this.statTicks.removeAt(0)
        }
        var sum: Long = 0
        for (l in this.statTicks) {
            sum += l
        }
        this.averageEnergyPerTick = sum / this.statTicks.size
    }
}