package net.modcrafters.mclib.ingredients

import net.minecraftforge.fluids.FluidStack

interface IFluidIngredient : IMachineIngredient {
    val fluidStack: FluidStack
}
