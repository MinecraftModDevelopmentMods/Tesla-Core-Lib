package net.modcrafters.mclib.recipes

import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IPositionedIngredient

interface IMachineRecipeShaped : IMachineRecipeInput {
    val width: Int get() = 3
    val height: Int get() = 3

    val ingredients: Array<IPositionedIngredient>
    fun getIngredient(column: Int, row: Int): IMachineIngredient?
}