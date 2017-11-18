package net.modcrafters.mclib.inventory

import net.modcrafters.mclib.ingredients.IMachineIngredient

interface IMachineInventory {
    val key: String

    val slots: Int
    fun getIngredient(slot: Int): IMachineIngredient

    fun extract(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean): Int
}
