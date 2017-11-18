package net.modcrafters.mclib.ingredients.implementations

import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IPositionedIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

class PositionedIngredient(override val slot: Int, private val ingredient: IMachineIngredient) : BaseDelegatedIngredient(), IPositionedIngredient {
    override fun getIngredient() = this.ingredient

    override fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch) =
        this.getIngredient().isMatch(ingredient, amountMatch)

    override val amount: Int
        get() = this.ingredient.amount
}
