package net.modcrafters.mclib.ingredients.implementations

import net.modcrafters.mclib.ingredients.IEnergyIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

abstract class BaseEnergyIngredient: IEnergyIngredient {
    override fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch) =
        when (ingredient) {
            is IEnergyIngredient -> amountMatch.compare(this.amount, ingredient.amount)
            else -> false
        }
}