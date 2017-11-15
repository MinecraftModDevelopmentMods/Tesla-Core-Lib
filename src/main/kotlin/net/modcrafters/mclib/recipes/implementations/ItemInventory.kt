package net.modcrafters.mclib.recipes.implementations

import net.minecraftforge.items.IItemHandler
import net.modcrafters.mclib.recipes.IFluidIngredient
import net.modcrafters.mclib.recipes.IItemIngredient
import net.modcrafters.mclib.recipes.IItemInventory
import net.modcrafters.mclib.recipes.IRecipeIngredient

open class ItemInventory(override val key: String, private val handler: IItemHandler) : IItemInventory {
    override fun extract(ingredient: IRecipeIngredient, fromSlot: Int, simulate: Boolean): Int {
        val source = this.getSlotContent(fromSlot)
        when (ingredient) {
            is IItemIngredient -> {
//                val stack = ingredient.itemStack

            }
            is IFluidIngredient -> {

            }
        }
        return 0
    }

    override val slots get() = this.handler.slots
    override fun getSlotContent(slot: Int) = this.handler.getStackInSlot(slot)
}
