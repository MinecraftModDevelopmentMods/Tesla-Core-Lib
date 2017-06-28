package net.ndrei.teslacorelib.containers

import net.minecraft.inventory.Slot

/**
 * Created by CF on 2017-06-28.
 */
interface IContainerSlotsProvider {
    fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot>
}
