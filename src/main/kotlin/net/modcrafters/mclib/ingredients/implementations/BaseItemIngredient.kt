package net.modcrafters.mclib.ingredients.implementations

import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.modcrafters.mclib.ingredients.IFluidIngredient
import net.modcrafters.mclib.ingredients.IItemIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

abstract class BaseItemIngredient : IItemIngredient {
    override fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch) =
        when (ingredient) {
            is IItemIngredient -> this.isMatchItem(ingredient, amountMatch)
            is IFluidIngredient -> this.isMatchFluid(ingredient, amountMatch)
            else -> false
        }

    protected open fun isMatchItem(ingredient: IItemIngredient, amountMatch: IngredientAmountMatch) =
        this.itemStacks.any { mine ->
            ingredient.itemStacks.any { other ->
                mine.isItemEqual(other) && amountMatch.compare(mine.count, other.count)
            }
        }

    protected open fun isMatchFluid(ingredient: IFluidIngredient, amountMatch: IngredientAmountMatch) = this.itemStacks.let {
        if ((it.size == 1) && it[0].hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            val cap = it[0].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
            if (cap != null) {
                val fluid = cap.drain(ingredient.fluidStack, false)
                when (amountMatch) {
                    IngredientAmountMatch.EXACT -> {
                        fluid?.amount == ingredient.fluidStack.amount
                    }
                    IngredientAmountMatch.BE_ENOUGH -> {
                        fluid?.amount == ingredient.fluidStack.amount
                    }
                    IngredientAmountMatch.IGNORE_SIZE -> {
                        (fluid?.amount ?: 0) > 0
                    }
                }
            }
            else false
        }
        else false
    }
}
