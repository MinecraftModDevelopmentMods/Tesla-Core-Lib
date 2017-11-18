package net.modcrafters.mclib.ingredients.implementations

import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

object NoIngredient: IMachineIngredient {
    override fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch) =
        ingredient === NoIngredient

    override val amount = 0
}
