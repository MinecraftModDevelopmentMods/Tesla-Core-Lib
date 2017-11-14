package net.modcrafters.mclib.recipes

interface IPositionedIngredient {
    val ingredient: IRecipeIngredient
    val slot: Int
}
