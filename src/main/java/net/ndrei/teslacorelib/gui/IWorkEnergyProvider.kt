package net.ndrei.teslacorelib.gui

/**
 * Created by CF on 2017-06-27.
 */
interface IWorkEnergyProvider {
    val workEnergyCapacity: Long
    val workEnergyStored: Long
    val workEnergyTick: Long
}