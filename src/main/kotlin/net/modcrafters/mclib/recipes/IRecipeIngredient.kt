package net.modcrafters.mclib.recipes

interface IRecipeIngredient {
    fun isMatch(inventory: IRecipeInventory, slot: Int): Boolean
}
