package net.ndrei.teslacorelib.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib

/**
 * Created by CF on 2017-06-22.
 */
abstract class BaseGearItem(modId: String, creativeTab: CreativeTabs, private val materialName: String) : RegisteredItem(modId, creativeTab, "gear_" + materialName.toLowerCase()) {
    constructor(materialName: String): this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName)

    override fun register(registry: IForgeRegistry<Item>) {
        super.register(registry)

        OreDictionary.registerOre("gear${this.materialName.capitalize()}", this)
        OreDictionary.registerOre("gear_${this.materialName.toLowerCase()}", this)
        OreDictionary.registerOre("${this.materialName.toLowerCase()}Gear", this)
        OreDictionary.registerOre("${this.materialName.toLowerCase()}_gear", this)
    }

    override fun getItemStackLimit(stack: ItemStack?): Int = 16

    fun oreDictName(): String = "gear${this.materialName.capitalize()}";
}
