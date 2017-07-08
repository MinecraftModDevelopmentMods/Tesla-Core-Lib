package net.ndrei.teslacorelib.items.sheets

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-29.
 */
abstract class ColoredSheetItem(modId: String, creativeTabs: CreativeTabs, val materialName: String, val color: Int)
    : RegisteredItem(modId, creativeTabs, "sheet_$materialName"), IItemColor {

    constructor(materialName: String, color: Int)
        : this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName, color)

    override final fun registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                ModelResourceLocation(ResourceLocation(TeslaCoreLib.MODID, "colored_sheet_base_16"), "inventory"))
    }

    override fun getColorFromItemstack(stack: ItemStack?, tintIndex: Int): Int {
        return this.color
    }
}