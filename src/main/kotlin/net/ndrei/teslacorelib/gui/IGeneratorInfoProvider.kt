package net.ndrei.teslacorelib.gui

interface IGeneratorInfoProvider {
    val generatedPowerCapacity: Long
    val generatedPowerStored: Long
    val generatedPowerReleaseRate: Long
}