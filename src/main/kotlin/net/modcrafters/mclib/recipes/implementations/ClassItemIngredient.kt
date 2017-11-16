package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ClassItemIngredient(classNameMask: String, val quantity: Int, val meta: Int): BaseFilterItemIngredient(classNameMask) {
    private val lazyItemStacks: List<ItemStack> by lazy {
        Item.REGISTRY.filter {
            this.isMatch(it!!::class.java.simpleName!!)
        }.mapNotNull {
            ItemStack(it!!, this.quantity, this.meta)
        }
    }

    override fun getStringsFor(stack: ItemStack) = listOf(
        stack.item.javaClass.simpleName
    )

    override val itemStacks: List<ItemStack>
        get() = this.lazyItemStacks
}