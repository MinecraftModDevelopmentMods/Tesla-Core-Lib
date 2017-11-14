package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.modcrafters.mclib.recipes.implementations.FluidInventory
import net.modcrafters.mclib.recipes.implementations.ItemInventory

fun createRecipeInventories(itemHandlers: Map<String, IItemHandler>, fluidHandlers: Map<String, IFluidHandler>): Array<IRecipeInventory> {
    val inventories = mutableListOf<IRecipeInventory>()
    itemHandlers.mapTo(inventories) { it.value.createRecipeInventory(it.key) }
    fluidHandlers.mapTo(inventories) { it.value.createRecipeInventory(it.key) }
    return inventories.toTypedArray()
}

fun IItemHandler.createRecipeInventory(key: String) = ItemInventory(key, this)
fun IFluidHandler.createRecipeInventory(key: String) = FluidInventory(key, this)