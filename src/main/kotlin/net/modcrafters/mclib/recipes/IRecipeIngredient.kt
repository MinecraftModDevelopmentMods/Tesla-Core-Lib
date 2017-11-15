package net.modcrafters.mclib.recipes

interface IRecipeIngredient {
    fun isMatch(inventory: IRecipeInventory, slot: Int, ignoreSize: Boolean = false): Boolean
}
