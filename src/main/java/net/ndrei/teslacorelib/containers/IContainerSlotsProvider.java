package net.ndrei.teslacorelib.containers;

import net.minecraft.inventory.Slot;

import java.util.List;

/**
 * Created by CF on 2016-12-21.
 */
public interface IContainerSlotsProvider {
    List<Slot> getSlots(BasicTeslaContainer container);
}
