package net.ndrei.teslacorelib.crafting

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreDictionary

class OreDictIngredientEx(private val oreNames: Array<String>) : Ingredient(0) {
    override fun getMatchingStacks(): Array<ItemStack> =
        oreNames
            .mapNotNull { OreDictionary.getOres(it) }
            .fold(mutableListOf<ItemStack>()) { list, it -> list.also { _ -> list.addAll(it) } }
            .toTypedArray()
}
