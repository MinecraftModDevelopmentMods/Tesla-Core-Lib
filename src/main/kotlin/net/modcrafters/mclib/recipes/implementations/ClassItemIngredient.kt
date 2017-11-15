package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ClassItemIngredient(classNameMask: String, val quantity: Int, val meta: Int): BaseFilterItemIngredient(classNameMask) {
    private val lazyItemStacks by lazy {
        Item.REGISTRY.filter {
            this.isMatch(it.javaClass.simpleName)
        }.map {
            ItemStack(it, this.quantity, this.meta)
        }
    }

    override fun getStringsFor(stack: ItemStack) = listOf(
        stack.item.javaClass.simpleName
    )

    override val itemStacks: List<ItemStack>
        get() = this.lazyItemStacks
}