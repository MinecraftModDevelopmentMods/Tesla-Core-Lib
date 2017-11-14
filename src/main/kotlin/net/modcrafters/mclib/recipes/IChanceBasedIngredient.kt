package net.modcrafters.mclib.recipes

import java.util.*

interface IChanceBasedIngredient {
    val chance: Float
    fun getIngredient(rand: Random): IRecipeIngredient?
}