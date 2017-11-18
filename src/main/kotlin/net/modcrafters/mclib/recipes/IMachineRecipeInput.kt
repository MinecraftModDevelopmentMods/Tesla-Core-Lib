package net.modcrafters.mclib.recipes

import net.modcrafters.mclib.inventory.IMachineInventory

interface IMachineRecipeInput {
    val key: String

    fun isMatch(inventory: IMachineInventory)
    fun process(inventory: IMachineInventory, simulate: Boolean)

    fun isInputFor(inventory: IMachineRecipeInput) =
        inventory.key == this.key
}
