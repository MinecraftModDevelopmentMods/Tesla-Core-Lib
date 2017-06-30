package net.ndrei.teslacorelib.items.gears

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-22.
 */
abstract class BaseGearItem(modId: String, creativeTab: CreativeTabs, val materialName: String)
    : RegisteredItem(modId, creativeTab, "gear_" + materialName.toLowerCase()) {

    constructor(materialName: String)
            : this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName)

    override fun getItemStackLimit(stack: ItemStack?): Int = 16

    fun oreDictName(): String = GearRegistry.getMaterial(this.materialName)?.oreDictName ?: "gear${this.materialName.capitalize()}"
}
