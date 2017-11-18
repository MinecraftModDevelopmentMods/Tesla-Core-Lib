package net.modcrafters.mclib.energy.implementations

import net.modcrafters.mclib.energy.IGenericEnergyStorage

open class GenericEnergyStorage(capacity: Long, protected var inputRate: Long, protected var outputRate: Long, initial: Long = 0)
    : IGenericEnergyStorage {
    private var storedPower = initial
    override var capacity = capacity
        protected set

    override var stored
        get() = this.storedPower
    protected set(value) { this.storedPower = value }

    override val canGive get() = (this.inputRate > 0)

    override val canTake get() = (this.outputRate > 0)

    override fun givePower(power: Long, simulated: Boolean): Long {
        return this.givePower(power, simulated, false)
    }

    protected fun givePower(power: Long, simulated: Boolean, forced: Boolean): Long {
        val acceptedTesla = if (forced)
            Math.min(this.capacity - this.stored, power)
        else
            Math.min(this.capacity - this.stored, Math.min(this.inputRate, power))

        if (!simulated) {
            val old = this.storedPower
            this.storedPower += acceptedTesla
            if (old != this.storedPower) {
                this.onChanged(old, this.storedPower)
            }
        }

        return acceptedTesla
    }

    override fun takePower(power: Long, simulated: Boolean): Long {
        return this.takePower(power, simulated, false)
    }

    protected fun takePower(power: Long, simulated: Boolean, forced: Boolean): Long {
        val removedPower = if (forced)
            Math.min(this.stored, power)
        else
            Math.min(this.stored, Math.min(this.outputRate, power))

        if (!simulated) {
            val old = this.storedPower
            this.storedPower -= removedPower
            if (old != this.storedPower) {
                this.onChanged(old, this.storedPower)
            }
        }

        return removedPower
    }

    open fun onChanged(old: Long, current: Long) {
    }
}