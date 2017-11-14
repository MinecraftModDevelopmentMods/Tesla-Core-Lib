package net.modcrafters.mclib.recipes.implementations

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.modcrafters.mclib.recipes.IRecipeIngredient

open class ToolInventory(key: String, handler: IItemHandler, private val slot: Int = 0): ItemInventory(key, handler) {
    override val slots: Int get() = 1

    override fun getSlotContent(slot: Int): ItemStack {
        if (slot != 0) throw IndexOutOfBoundsException("This inventory handles only 1 slot!")
        return super.getSlotContent(this.slot)
    }

    override fun findMatch(ingredient: IRecipeIngredient, startIndex: Int): Int {
        val index = super.findMatch(ingredient, startIndex)
        return if (index < 0) -1 else 0
    }

    override fun extract(ingredient: IRecipeIngredient, fromSlot: Int, simulate: Boolean): Int {
        if (slot != 0) throw IndexOutOfBoundsException("This inventory handles only 1 slot!")
        return super.extract(ingredient, this.slot, simulate)
    }
}
