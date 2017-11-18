package net.modcrafters.mclib.energy

interface IGenericEnergyStorage {
    val capacity: Long
    val stored: Long

    val canGive: Boolean
    fun givePower(power: Long, simulated: Boolean): Long
    fun tryGive() = 1L == this.givePower(1L, true)

    val canTake: Boolean
    fun takePower(power: Long, simulated: Boolean): Long
    fun tryTake() = 1L == this.takePower(1L, true)
}