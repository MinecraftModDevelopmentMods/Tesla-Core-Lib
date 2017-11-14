package net.modcrafters.mclib.recipes

import net.minecraft.item.ItemStack

interface IItemIngredient : IRecipeIngredient {
    val itemStack: ItemStack
}
