package net.modcrafters.mclib.recipes

import net.minecraft.item.ItemStack

interface IItemIngredient : IRecipeIngredient, IItemIngredientMatcher {
    val itemStacks: List<ItemStack>

    override fun isMatch(inventory: IRecipeInventory, slot: Int, ignoreSize: Boolean): Boolean {
        if (inventory is IItemInventory) {
            val stack = inventory.getSlotContent(slot)
            return this.isMatch(stack, ignoreSize)
        }
        return false
    }
}
