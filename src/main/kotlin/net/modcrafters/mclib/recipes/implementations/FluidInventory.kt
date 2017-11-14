package net.modcrafters.mclib.recipes.implementations

import net.minecraftforge.fluids.capability.IFluidHandler
import net.modcrafters.mclib.recipes.IFluidInventory
import net.modcrafters.mclib.recipes.IRecipeIngredient

class FluidInventory(override val key: String, private val handler: IFluidHandler) : IFluidInventory {
    override val slots get() = this.handler.tankProperties.size

    override fun getSlotContent(slot: Int) = this.handler.tankProperties[slot].contents

    override fun extract(ingredient: IRecipeIngredient, fromSlot: Int, simulate: Boolean): Int {
        return 0
    }
}