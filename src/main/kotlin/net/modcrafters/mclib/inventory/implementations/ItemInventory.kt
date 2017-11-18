package net.modcrafters.mclib.inventory.implementations

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.modcrafters.mclib.ingredients.IFluidIngredient
import net.modcrafters.mclib.ingredients.IItemIngredient
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.IngredientAmountMatch
import net.modcrafters.mclib.inventory.IItemInventory

open class ItemInventory(override val key: String, private val handler: IItemHandler) : IItemInventory {
    override fun extractStack(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean): ItemStack {
        val source = this.getSlotContent(fromSlot)
        if (ingredient.isMatch(this, fromSlot, IngredientAmountMatch.BE_ENOUGH)) {
            when (ingredient) {
                is IItemIngredient -> {

                }
                is IFluidIngredient -> {
                    // TODO: handle this
                }
            }
        }
        return ItemStack.EMPTY
    }

    override val slots get() = this.handler.slots
    override fun getSlotContent(slot: Int) = this.handler.getStackInSlot(slot)
}
