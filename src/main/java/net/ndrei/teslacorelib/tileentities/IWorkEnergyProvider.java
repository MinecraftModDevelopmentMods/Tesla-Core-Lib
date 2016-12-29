package net.ndrei.teslacorelib.tileentities;

/**
 * Created by CF on 2016-12-03.
 */
public interface IWorkEnergyProvider {
    long getWorkEnergyCapacity();
    long getWorkEnergyStored();
    long getWorkEnergyTick();
}
