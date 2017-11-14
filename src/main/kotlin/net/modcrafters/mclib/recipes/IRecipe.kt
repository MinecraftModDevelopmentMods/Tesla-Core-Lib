package net.modcrafters.mclib.recipes

interface IRecipe {
    val inputs: Array<IRecipeInput>

    val primaryOutput: IRecipeIngredient
    val secondaryOutput: Array<IRecipeIngredient>
}
