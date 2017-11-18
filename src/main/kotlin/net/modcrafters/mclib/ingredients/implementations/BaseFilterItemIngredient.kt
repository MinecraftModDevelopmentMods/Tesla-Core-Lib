package net.modcrafters.mclib.ingredients.implementations

import net.minecraft.item.ItemStack
import net.modcrafters.mclib.ingredients.IFluidIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

abstract class BaseFilterItemIngredient(val mask: String) : BaseItemIngredient() {
    abstract fun getStringsFor(stack: ItemStack): List<String>

    override fun isMatchFluid(ingredient: IFluidIngredient, amountMatch: IngredientAmountMatch) = false

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
