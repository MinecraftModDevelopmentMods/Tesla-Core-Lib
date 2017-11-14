package net.modcrafters.mclib.recipes

import net.minecraft.item.ItemStack

interface IItemInventory: IRecipeInventory {
    fun getSlotContent(slot: Int): ItemStack
}
