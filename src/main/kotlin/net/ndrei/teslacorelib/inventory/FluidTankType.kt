package net.ndrei.teslacorelib.inventory

enum class FluidTankType(val canDrain: Boolean, val canFill: Boolean) {
    INPUT(false, true),
    OUTPUT(true, false),
    BOTH(true, true)
}