package net.modcrafters.mclib.ingredients.implementations

import net.minecraft.item.ItemStack

class ItemIngredient(vararg val stacks: ItemStack): BaseItemIngredient() {
    override val itemStacks: List<ItemStack>
        get() = this.stacks.toList()

    override val amount: Int
        get() = this.stacks.firstOrNull()?.count ?: 0
}
