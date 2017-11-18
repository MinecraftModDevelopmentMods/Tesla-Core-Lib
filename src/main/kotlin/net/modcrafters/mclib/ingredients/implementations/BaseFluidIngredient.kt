package net.modcrafters.mclib.ingredients.implementations

import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.modcrafters.mclib.ingredients.IFluidIngredient
import net.modcrafters.mclib.ingredients.IItemIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch

abstract class BaseFluidIngredient: IFluidIngredient {
    override fun isMatch(ingredient: IMachineIngredient, amountMatch: IngredientAmountMatch) =
        when (ingredient) {
            is IItemIngredient -> {
                val other = ingredient.itemStacks
                if ((other.size == 1) && other[0].hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                    val cap = other[0].getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                    if (cap != null) {
                        when (amountMatch) {
                            IngredientAmountMatch.IGNORE_SIZE -> {
                                val fluid = cap.drain(this.fluidStack, false)
                                (fluid != null) && (fluid.amount > 0)
                            }
                            IngredientAmountMatch.BE_ENOUGH -> {
                                val fluid = cap.drain(this.fluidStack, false)
                                (fluid != null) && (fluid.amount == this.fluidStack.amount)
                            }
                            IngredientAmountMatch.EXACT -> {
                                val fluid = cap.drain(this.fluidStack, false)
                                (fluid != null) && (fluid.amount == this.fluidStack.amount) &&
                                    (cap.tankProperties.sumBy { it.contents?.amount ?: 0 } == this.fluidStack.amount)
                            }
                        }
                    } else false
                } else false
            }
            is IFluidIngredient -> {
                val other = ingredient.fluidStack
                this.fluidStack.isFluidEqual(other) && amountMatch.compare(this.fluidStack.amount, other.amount)
            }
            else -> false
        }

    override val amount: Int
        get() = this.fluidStack.amount
}
