package net.modcrafters.mclib.recipes

import net.minecraft.item.ItemStack

interface IItemIngredientMatcher {
    fun isMatch(stack: ItemStack, ignoreSize: Boolean = false): Boolean
}