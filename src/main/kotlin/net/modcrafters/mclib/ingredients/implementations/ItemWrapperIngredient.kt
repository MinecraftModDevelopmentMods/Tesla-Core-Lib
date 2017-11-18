package net.modcrafters.mclib.ingredients.implementations

import net.minecraft.item.ItemStack

class ItemWrapperIngredient(private val stack: ItemStack): BaseItemIngredient() {
    override val itemStacks: List<ItemStack>
        get() = listOf(this.stack)

    override val amount: Int
        get() = this.stack.count
}
