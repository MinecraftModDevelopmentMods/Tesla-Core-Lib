package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.FluidStack
import net.ndrei.teslacorelib.utils.isEnough

interface IFluidIngredient : IRecipeIngredient, IFluidIngredientMatcher {
    val fluidStack: FluidStack

    override fun isMatch(inventory: IRecipeInventory, slot: Int, ignoreSize: Boolean) =
        when (inventory) {
            is IFluidInventory -> {
                val stack = inventory.getSlotContent(slot)
                if (stack == null) false else this.isMatch(stack, ignoreSize)
            }
            else -> false
        }

    override fun isMatch(stack: FluidStack, ignoreSize: Boolean) =
        this.fluidStack.isEnough(stack, ignoreSize)
}
