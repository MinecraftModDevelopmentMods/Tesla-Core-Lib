package net.modcrafters.mclib.ingredients.implementations

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class ClassItemIngredient(classNameMask: String, override val amount: Int, val meta: Int): BaseFilterItemIngredient(classNameMask) {
    private val lazyItemStacks: List<ItemStack> by lazy {
        Item.REGISTRY.filter {
            this.isMatch(it!!::class.java.simpleName!!)
        }.mapNotNull {
            ItemStack((it as Item), this.amount, this.meta)
        }
    }

    override fun getStringsFor(stack: ItemStack) = listOf(
        stack.item.javaClass.simpleName
    )

    override val itemStacks: List<ItemStack>
        get() = this.lazyItemStacks
}