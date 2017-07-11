package net.ndrei.teslacorelib.items.gears

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate

/**
 * Created by CF on 2017-06-29.
 */
abstract class ColoredGearItem(modId: String, creativeTabs: CreativeTabs, materialName: String, val color: Int, val baseGear: String? = null, val ingot: String? = null)
    : BaseGearItem(modId, creativeTabs, materialName), IItemColorDelegate {

    constructor(materialName: String, color: Int, baseGear: String? = null, ingot: String? = null)
        : this(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, materialName, color, baseGear, ingot)

    override val recipe: IRecipe?
        get() = if (!this.baseGear.isNullOrBlank() && !this.ingot.isNullOrBlank()) ShapedOreRecipe(
                null, ItemStack(this, 1),
                " w ",
                "wsw",
                " w ",
                'w', this.ingot,
                's', this.baseGear
        ) else super.recipe

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