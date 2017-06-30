package net.ndrei.teslacorelib.inventory

import mekanism.api.energy.IStrictEnergyAcceptor
import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
import net.darkhax.tesla.capability.TeslaCapabilities
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Optional
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig

/**
 * Created by CF on 2017-06-28.
 */
@Optional.InterfaceList(
        Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
        Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism")
)
open class EnergyStorage(maxStoredEnergy: Long, inputRate: Long, outputRate: Long)
    : ITeslaConsumer, ITeslaHolder, ITeslaProducer, IStrictEnergyAcceptor, IEnergyStorage, INBTSerializable<NBTTagCompound>, ICapabilityProvider, IEnergyStatistics {
    private var stored: Long = 0
    private var capacity: Long = 0

    private var inputRate: Long = 0
    private var outputRate: Long = 0

    var color: EnumDyeColor? = null
        private set

    private var sidedConfig: ISidedItemHandlerConfig? = null

    private var statStored: Long = 0
    override final var averageEnergyPerTick: Long = 0
        private set
    override final var lastTickEnergy: Long = 0
        private set
    private val statTicks = mutableListOf<Long>()

    init {
        this.color = color
        this.capacity = maxStoredEnergy
        this.inputRate = Math.max(0, inputRate)
        this.outputRate = Math.max(0, outputRate)
    }

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
        get() = this.getCapacity() == this.storedPower

    val isEmpty: Boolean
        get() = this.storedPower == 0L

    //region forge energy

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        return this.givePower(maxReceive.toLong(), simulate).toInt()
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        return this.takePower(maxExtract.toLong(), simulate).toInt()
    }

    override fun getEnergyStored(): Int {
        return Math.min(Integer.MAX_VALUE.toLong(), this.storedPower).toInt()
    }

    override fun getMaxEnergyStored(): Int {
        return Math.min(Integer.MAX_VALUE.toLong(), this.getCapacity()).toInt()
    }

    override fun canExtract(): Boolean {
        return this.getOutputRate() > 0
    }

    override fun canReceive(): Boolean {
        return this.getInputRate() > 0
    }

    //endregion
    //region IStrictEnergyAcceptor

    @Optional.Method(modid = "Mekanism")
    override fun getEnergy(): Double {
        return this.energyStored.toDouble()
    }

    @Optional.Method(modid = "Mekanism")
    override fun setEnergy(energy: Double) {
        // TODO: ??
    }

    @Optional.Method(modid = "Mekanism")
    override fun getMaxEnergy(): Double {
        return this.maxEnergyStored.toDouble()
    }

    @Optional.Method(modid = "Mekanism")
    override fun transferEnergyToAcceptor(side: EnumFacing, amount: Double): Double {
        var tesla = Math.round(amount.toFloat() * .4f)
        tesla = this.receiveEnergy(tesla, false)
        return tesla / .4
    }

    @Optional.Method(modid = "Mekanism")
    override fun canReceiveEnergy(side: EnumFacing): Boolean {
        return this.canReceive()
    }

    //endregion

    override fun getStoredPower(): Long {
        return this.stored
    }

    override fun givePower(tesla: Long, simulated: Boolean): Long {
        return this.givePower(tesla, simulated, false)
    }

    private fun givePower(tesla: Long, simulated: Boolean, forced: Boolean): Long {
        val acceptedTesla = if (forced)
            Math.min(this.getCapacity() - this.stored, tesla)
        else
            Math.min(this.getCapacity() - this.stored, Math.min(this.getInputRate(), tesla))

        if (!simulated) {
            this.stored += acceptedTesla
            this.onChanged()
        }

        return acceptedTesla
    }

    override fun takePower(tesla: Long, simulated: Boolean): Long {
        return this.takePower(tesla, simulated, false)
    }

    private fun takePower(tesla: Long, simulated: Boolean, forced: Boolean): Long {
        val removedPower = if (forced)
            Math.min(this.stored, tesla)
        else
            Math.min(this.stored, Math.min(this.getOutputRate(), tesla))

        if (!simulated) {
            this.stored -= removedPower
            this.onChanged()
        }

        return removedPower
    }

    override fun getCapacity(): Long {
        return this.capacity
    }

    //region util method

    /**
     * Sets the capacity of the the container. If the existing stored power is more than the
     * new capacity, the stored power will be decreased to match the new capacity.

     * @param capacity The new capacity for the container.
     * *
     * @return The instance of the container being updated.
     */
    fun setCapacity(capacity: Long): EnergyStorage {
        this.capacity = capacity

        if (this.stored > capacity)
            this.stored = capacity

        this.onChanged()
        return this
    }

    /**
     * Gets the maximum amount of Tesla power that can be accepted by the container.

     * @return The amount of Tesla power that can be accepted at any time.
     */
    fun getInputRate(): Long {
        return this.inputRate
    }

    /**
     * Sets the maximum amount of Tesla power that can be accepted by the container.

     * @param rate The amount of Tesla power to accept at a time.
     * *
     * @return The instance of the container being updated.
     */
    fun setInputRate(rate: Long): EnergyStorage {
        this.inputRate = rate
        return this
    }

    /**
     * Gets the maximum amount of Tesla power that can be pulled from the container.

     * @return The amount of Tesla power that can be extracted at any time.
     */
    fun getOutputRate(): Long {
        return this.outputRate
    }

    /**
     * Sets the maximum amount of Tesla power that can be pulled from the container.

     * @param rate The amount of Tesla power that can be extracted.
     * *
     * @return The instance of the container being updated.
     */
    fun setOutputRate(rate: Long): EnergyStorage {
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
    fun setTransferRate(rate: Long): EnergyStorage {
        this.setInputRate(rate)
        this.setOutputRate(rate)
        return this
    }

    //endregion

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

        if (this.stored > this.getCapacity())
            this.stored = this.getCapacity()

        if (this.stored != originalStored) {
            this.onChanged()
        }
    }

    open fun onChanged() {}

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (this.sidedConfig != null && this.color != null && this.sidedConfig!!.isSideSet(this.color!!, facing!!)) {
            if (capability === CapabilityEnergy.ENERGY) {
                return true
            } else if (Loader.isModLoaded("tesla") && this.hasTeslaCapability(capability)) {
                return true
            } else if (Loader.isModLoaded("Mekanism") && capability.name == "mekanism.api.energy.IStrictEnergyAcceptor") {
                return true // TODO: not sure if this is the best way :S
            }
        }

        return false
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (this.sidedConfig != null && this.color != null && this.sidedConfig!!.isSideSet(this.color!!, facing!!)) {
            if (capability === CapabilityEnergy.ENERGY) {
                return this as T
            } else if (Loader.isModLoaded("tesla") && this.hasTeslaCapability(capability)) {
                return this as T
            } else if (Loader.isModLoaded("Mekanism") && capability.name == "mekanism.api.energy.IStrictEnergyAcceptor") {
                return this as T
            }
        }

        return null
    }

    @Optional.Method(modid = "tesla")
    private fun hasTeslaCapability(capability: Capability<*>): Boolean {
        if (capability === TeslaCapabilities.CAPABILITY_HOLDER) {
            return true
        } else if (this.getInputRate() > 0 && capability === TeslaCapabilities.CAPABILITY_CONSUMER) {
            return true
        } else if (this.getOutputRate() > 0 && capability === TeslaCapabilities.CAPABILITY_PRODUCER) {
            return true
        }

        return false
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