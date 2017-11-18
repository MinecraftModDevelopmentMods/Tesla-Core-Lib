package net.modcrafters.mclib.ingredients

import net.minecraft.item.ItemStack

interface IItemIngredient : IMachineIngredient {
    val itemStacks: List<ItemStack>
}
