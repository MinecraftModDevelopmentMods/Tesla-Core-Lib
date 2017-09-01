package net.ndrei.teslacorelib.crafting

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext

class OreDictIngredientExFactory : IIngredientFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient {
        val oreNames = JsonUtils.getString(json, "ore", "")
            .split(',')
            .mapNotNull { it.trim().let { if (it.isBlank()) null else it } }
            .toTypedArray()
        return if (oreNames.isNotEmpty()) {
            OreDictIngredientEx(oreNames)
        }
        else
            throw JsonSyntaxException("No ore names found.")
    }
}
