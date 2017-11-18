package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.modcrafters.mclib.inventory.IMachineInventory
import net.modcrafters.mclib.inventory.implementations.FluidInventory
import net.modcrafters.mclib.inventory.implementations.ItemInventory

fun createRecipeInventories(itemHandlers: Map<String, IItemHandler>, fluidHandlers: Map<String, IFluidHandler>): Array<IMachineInventory> {
    val inventories = mutableListOf<IMachineInventory>()
    itemHandlers.mapTo(inventories) { it.value.createRecipeInventory(it.key) }
    fluidHandlers.mapTo(inventories) { it.value.createRecipeInventory(it.key) }
    return inventories.toTypedArray()
}

fun IItemHandler.createRecipeInventory(key: String) = ItemInventory(key, this)
fun IFluidHandler.createRecipeInventory(key: String) = FluidInventory(key, this)