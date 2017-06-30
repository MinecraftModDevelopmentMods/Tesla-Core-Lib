package net.ndrei.teslacorelib.items.gears

import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.COLOR_STONE
import net.ndrei.teslacorelib.MATERIAL_STONE
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
object GearStoneItem : ColoredGearItem(MATERIAL_STONE, COLOR_STONE) {
    override val recipes: List<IRecipe>
        get() {
            val recipes = super.recipes.toMutableList()
            arrayOf("cobblestone", Blocks.STONE, "stone", Blocks.MOSSY_COBBLESTONE, Blocks.STONEBRICK, Blocks.SANDSTONE, Blocks.RED_SANDSTONE).forEach {
                recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
                        " w ",
                        "wsw",
                        " w ",
                        'w', it,
                        's', GearWoodItem.oreDictName()
                ))
//                recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
//                        " w ",
//                        "wsw",
//                        " w ",
//                        'w', it,
//                        's', "stickWood"
//                ))
            }
            return recipes.toList()
        }
}
