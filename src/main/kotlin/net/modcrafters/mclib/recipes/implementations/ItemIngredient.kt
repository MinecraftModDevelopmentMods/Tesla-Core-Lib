package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.ItemStack
import net.modcrafters.mclib.recipes.IItemIngredient
import net.ndrei.teslacorelib.utils.isEnough

class ItemIngredient(vararg val stacks: ItemStack): IItemIngredient {
    override val itemStacks: List<ItemStack>
        get() = this.stacks.toList()

    override fun isMatch(stack: ItemStack, ignoreSize: Boolean) =
        this.stacks.any { it.isEnough(stack, ignoreSize) }
}
