package net.modcrafters.mclib.ingredients

import net.modcrafters.mclib.inventory.IMachineInventory

interface IMachineIngredient {
    fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch): Boolean
    fun isSame(ingredient: IMachineIngredient) = this.isMatch(ingredient, IngredientAmountMatch.EXACT)
    fun isEnough(ingredient: IMachineIngredient) = this.isMatch(ingredient, IngredientAmountMatch.BE_ENOUGH)
    fun isSameIngredient(ingredient: IMachineIngredient) = this.isMatch(ingredient, IngredientAmountMatch.IGNORE_SIZE)

    fun isMatch(inventory: IMachineInventory, slot: Int, amountMatch: IngredientAmountMatch = IngredientAmountMatch.BE_ENOUGH) =
        this.isMatch(inventory.getIngredient(slot), amountMatch)

    val amount: Int
}
