package net.ndrei.teslacorelib.crafting

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreDictionary

class OreDictIngredientExFactory : IIngredientFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient {
        val oreNames = JsonUtils.getString(json, "ore", "")
            .split(',')
            .mapNotNull { it.trim().let { if (it.isBlank()) null else it } }
            .toTypedArray()
        return if (oreNames.isNotEmpty()) {
            Ingredient.fromStacks(*getMatchingStacks(*oreNames))
        }
        else
            throw JsonSyntaxException("No ore names found.")
    }

    companion object {
        fun getMatchingStacks(vararg oreNames: String): Array<ItemStack> {
            val ingredients = mutableListOf<ItemStack>()

            oreNames.forEach {
                var added = false
                if (it.contains(':')) {
                    // probably a registry name
                    val parts = it.split(':')
                    val meta = if (parts.size > 2) {
                        parts[2].toIntOrNull() ?: 0
                    } else 0
                    val item = Item.REGISTRY.getObject(ResourceLocation(parts[0], parts[1]))
                    if (item != null) {
                        ingredients.add(ItemStack(item, 1, meta))
                        added = true
                    }
                }
                if (!added) {
                    OreDictionary.getOres(it)?.forEach {
                        ingredients.add(it)
                    }
                }
            }

            return ingredients.toTypedArray()
        }
    }
}
