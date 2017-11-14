package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.FluidStack

interface IFluidIngredient : IRecipeIngredient {
    val fluidStack: FluidStack
}
