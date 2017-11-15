package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.FluidStack

interface IFluidIngredientMatcher {
    fun isMatch(stack: FluidStack, ignoreSize: Boolean = false): Boolean
}