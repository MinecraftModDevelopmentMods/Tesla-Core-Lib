package net.modcrafters.mclib.recipes

interface IRecipeIngredientStringFactory: IRecipeIngredientFactory {
    fun isMatch(info: String): Boolean
    fun getIngredient(info: String): IRecipeIngredient?
}
