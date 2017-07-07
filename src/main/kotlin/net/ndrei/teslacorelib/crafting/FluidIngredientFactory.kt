package net.ndrei.teslacorelib.crafting

import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.fluids.FluidRegistry

/**
 * Created by CF on 2017-07-07.
 */
class FluidIngredientFactory : IIngredientFactory {
    override fun parse(context: JsonContext?, json: JsonObject?): Ingredient {
        val fluidName = JsonUtils.getString(json, "fluid")
        val fluid = FluidRegistry.getFluid(fluidName)
        return if (fluid != null) {
            FluidIngredient(fluid)
        }
        else
            throw JsonSyntaxException("Fluid '$fluidName' not found.")
    }
}