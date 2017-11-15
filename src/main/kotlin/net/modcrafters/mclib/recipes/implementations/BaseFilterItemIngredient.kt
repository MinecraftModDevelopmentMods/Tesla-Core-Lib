package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.ItemStack
import net.modcrafters.mclib.recipes.IItemIngredient

abstract class BaseFilterItemIngredient(val mask: String) : IItemIngredient {
    abstract fun getStringsFor(stack: ItemStack): List<String>

    override fun isMatch(stack: ItemStack, ignoreSize: Boolean): Boolean {
        if (!stack.isEmpty) {
            val strings = this.getStringsFor(stack)
            val matched = strings.firstOrNull { this.isMatch(it) }
            return !matched.isNullOrEmpty()
        }
        return false
    }

    protected open fun isMatch(info: String): Boolean {
        if (this.mask.startsWith('*')) {
            if (this.mask.endsWith('*'))
                return info.contains(this.mask.substring(1 .. (this.mask.length - 2)), false)
            return info.endsWith(this.mask.substring(1))
        }
        else if (this.mask.endsWith('*'))
            return info.startsWith(this.mask.substring(0 .. (this.mask.length - 2)))
        return info == this.mask
    }
}
