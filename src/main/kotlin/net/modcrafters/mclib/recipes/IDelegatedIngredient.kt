package net.modcrafters.mclib.recipes

interface IDelegatedIngredient: IRecipeIngredient {
    fun getIngredient(): IRecipeIngredient
}
