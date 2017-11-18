package net.modcrafters.mclib.inventory

import net.minecraftforge.fluids.FluidStack
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.asIngredient
import net.modcrafters.mclib.ingredients.implementations.NoIngredient

interface IFluidInventory: IMachineInventory {
    fun getSlotContent(slot: Int): FluidStack?

    fun extractFluid(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean): FluidStack?

    override fun extract(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean) =
        this.extractFluid(ingredient, fromSlot, simulate)?.amount ?: 0

    override fun getIngredient(slot: Int) = this.getSlotContent(slot)?.asIngredient() ?: NoIngredient
}
