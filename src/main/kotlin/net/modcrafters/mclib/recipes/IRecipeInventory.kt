package net.modcrafters.mclib.recipes

interface IRecipeInventory {
    val key: String
    val slots: Int

    fun findMatch(ingredient: IRecipeIngredient, startIndex: Int = 0): Int {
        (startIndex.coerceAtLeast(0) until this.slots).forEach {
            if (ingredient.isMatch(this, it))
                return it
        }
        return -1
    }

    fun extract(ingredient: IRecipeIngredient, fromSlot: Int, simulate: Boolean): Int
}
