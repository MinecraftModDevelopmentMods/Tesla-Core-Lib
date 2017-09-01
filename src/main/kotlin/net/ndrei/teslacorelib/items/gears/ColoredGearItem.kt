package net.ndrei.teslacorelib.items.gears

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate

/**
 * Created by CF on 2017-06-29.
 */
abstract class ColoredGearItem(modId: String, creativeTabs: CreativeTabs, materialName: String, val color: Int)
    : BaseGearItem(modId, creativeTabs, materialName), IItemColorDelegate {

    constructor(materialName: String, color: Int)
        : this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName, color)

    constructor(gearType: CoreGearType)
        : this(gearType.material, gearType.color)

    override final fun registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                ModelResourceLocation(ResourceLocation(TeslaCoreLib.MODID, "colored_gear_base"), "inventory"))
    }

    override fun getColorFromItemStack(stack: ItemStack, tintIndex: Int): Int {
        return this.color
    }
}
