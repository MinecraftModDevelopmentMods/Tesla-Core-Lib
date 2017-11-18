package net.modcrafters.mclib.ingredients

interface IDelegatedIngredient: IMachineIngredient {
    fun getIngredient(): IMachineIngredient
}
