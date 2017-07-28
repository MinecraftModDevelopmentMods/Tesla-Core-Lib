package net.ndrei.teslacorelib.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class BaseAddon protected constructor(modId: String, tab: CreativeTabs, registryName: String)
    : RegisteredItem(modId, tab, registryName) {
    open fun canBeAddedTo(machine: SidedTileEntity) = false

    open fun onAdded(addon: ItemStack, machine: SidedTileEntity) { }

    open fun onRemoved(addon: ItemStack, machine: SidedTileEntity) { }

    open fun isValid(machine: SidedTileEntity): Boolean = true

    open val workEnergyMultiplier: Float
        get() = 1.0f
}