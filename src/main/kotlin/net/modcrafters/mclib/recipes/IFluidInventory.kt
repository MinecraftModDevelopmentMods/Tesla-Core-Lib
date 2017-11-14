package net.modcrafters.mclib.recipes

import net.minecraftforge.fluids.FluidStack

interface IFluidInventory: IRecipeInventory {
    fun getSlotContent(slot: Int): FluidStack?
}
