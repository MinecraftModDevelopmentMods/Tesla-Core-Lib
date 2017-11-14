package net.modcrafters.mclib.recipes

interface IRecipeInput {
    val key: String

    fun isMatch(inventory: IRecipeInventory)
    fun process(inventory: IRecipeInventory, simulate: Boolean)

    fun isInput(inventory: IRecipeInput) =
        inventory.key == this.key
}
