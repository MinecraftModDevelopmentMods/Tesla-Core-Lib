package net.modcrafters.mclib.inventory.implementations

import net.minecraftforge.fluids.capability.IFluidHandler
import net.modcrafters.mclib.ingredients.IFluidIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.inventory.IFluidInventory

class FluidInventory(override val key: String, private val handler: IFluidHandler) : IFluidInventory {
    override val slots get() = this.handler.tankProperties.size
    override fun getSlotContent(slot: Int) = this.handler.tankProperties[slot].contents

    override fun extractFluid(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean) =
        when (ingredient) {
            is IFluidIngredient -> {
                // TODO: handle 'fromSlot' somehow (will usually be just 1 anyways)
                this.handler.drain(ingredient.fluidStack, !simulate)
            }
            // TODO: handle IItemIngredient
            else -> null
        }
}
