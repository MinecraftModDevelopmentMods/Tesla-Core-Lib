package net.modcrafters.mclib.ingredients

interface IMachineIngredientStringFactory : IMachineIngredientFactory {
    fun isMatch(info: String): Boolean
    fun getIngredient(info: String): IMachineIngredient?
}
