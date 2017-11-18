package net.modcrafters.mclib.recipes

import net.modcrafters.mclib.ingredients.IMachineIngredient

interface IMachineRecipeShapeless: IMachineRecipeInput {
    val ingredients: Array<IMachineIngredient>
}
