package net.ndrei.teslacorelib.energy

import net.ndrei.teslacorelib.energy.systems.*

enum class EnergySystem(val system: IEnergySystem) {
    FORGE(ForgeEnergySystem),
    TESLA(TeslaSystem),
    MEKANISM(MekanismSystem),
    RF(RFSystem),
    MJOULES(MJSystem);

    companion object {
        val ORDERED get() = arrayOf(FORGE, TESLA, RF, MJOULES, MEKANISM)
    }
}