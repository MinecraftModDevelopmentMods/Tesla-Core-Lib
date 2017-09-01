package net.ndrei.teslacorelib.crafting

import com.google.gson.JsonObject
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.oredict.OreDictionary
import java.util.function.BooleanSupplier

class OreDictConditionFactory : IConditionFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): BooleanSupplier {
        val oreNames = JsonUtils.getString(json, "ore", "")
            .split(',')
            .mapNotNull { it.trim().let { if (it.isBlank()) null else it } }
            .toTypedArray()

        return BooleanSupplier { oreNames.any { OreDictionary.getOres(it).isNotEmpty() } }
    }
}
