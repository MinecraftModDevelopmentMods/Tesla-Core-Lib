package net.ndrei.teslacorelib.items.powders

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-29.
 */
abstract class ColoredPowderItem(modId: String, creativeTabs: CreativeTabs, val materialName: String, val color: Int, val smeltingExperience: Float = 0.0f, val smeltingResult: String? = null)
    : RegisteredItem(modId, creativeTabs, "powder_$materialName"), IItemColorDelegate {

    constructor(materialName: String, color: Int, smeltingExperience: Float = 0.0f, smeltingResult: String? = null)
        : this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName, color, smeltingExperience, smeltingResult)

    override final fun registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                ModelResourceLocation(ResourceLocation(TeslaCoreLib.MODID, "colored_powder_base"), "inventory"))
    }

    override fun getColorFromItemStack(stack: ItemStack, tintIndex: Int): Int {
        return this.color
    }
}
