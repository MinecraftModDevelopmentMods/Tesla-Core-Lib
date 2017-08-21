@file:Suppress("unused")

package net.ndrei.teslacorelib.config

import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.JsonUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import net.ndrei.teslacorelib.utils.copyWithSize

fun JsonObject.readFluidStack(memberName: String): FluidStack? {
    val json = JsonUtils.getJsonObject(this, memberName) ?: return null

    val fluid = JsonUtils.getString(json, "name", "")
            .let { FluidRegistry.getFluid(it) } ?: return null
    val amount = JsonUtils.getInt(json, "quantity", 0)

    return if (amount <= 0) null else FluidStack(fluid, amount)
}

fun JsonObject.readItemStacks(memberName: String): List<ItemStack> {
    val json = JsonUtils.getJsonObject(this, memberName) ?: return listOf()
    return json.readItemStacks()
}

fun JsonObject.readItemStacks(): List<ItemStack> {
    val item = JsonUtils.getString(this, "name", "")
            .let {
                val registryName = if (it.isNullOrEmpty()) null else ResourceLocation(it)
                if ((registryName != null) && Item.REGISTRY.containsKey(registryName))
                    Item.REGISTRY.getObject(registryName)
                else null
            }
    if (item != null) {
        val meta = JsonUtils.getInt(this, "meta", 0)
        val amount = JsonUtils.getInt(this, "quantity", 1)
        return listOf(ItemStack(item, amount, meta))
    }
    else {
        val ore = JsonUtils.getString(this, "ore", "")
        if (!ore.isNullOrEmpty()) {
            val amount = JsonUtils.getInt(this, "quantity", 1)
            return OreDictionary.getOres(ore)
                    .map { it.copyWithSize(amount) }
        }
    }

    return listOf()
}

fun JsonObject.readItemStack(memberName: String) = this.readItemStacks(memberName).firstOrNull()

fun JsonObject.readItemStack() = this.readItemStacks().firstOrNull()
