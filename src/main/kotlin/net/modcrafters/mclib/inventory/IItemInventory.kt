package net.modcrafters.mclib.inventory

import net.minecraft.item.ItemStack
import net.modcrafters.mclib.ingredients.IMachineIngredient
import net.modcrafters.mclib.ingredients.asIngredient
import net.modcrafters.mclib.ingredients.implementations.NoIngredient

interface IItemInventory: IMachineInventory {
    fun getSlotContent(slot: Int): ItemStack

    fun extractStack(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean): ItemStack

    override fun extract(ingredient: IMachineIngredient, fromSlot: Int, simulate: Boolean) =
        this.extractStack(ingredient, fromSlot, simulate).count

    override fun getIngredient(slot: Int) = this.getSlotContent(slot).asIngredient() ?: NoIngredient
}
