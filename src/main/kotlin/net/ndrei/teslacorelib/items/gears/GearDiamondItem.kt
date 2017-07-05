package net.ndrei.teslacorelib.items.gears

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.MATERIAL_DIAMOND
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
object GearDiamondItem : ColoredGearItem(MATERIAL_DIAMOND, MaterialColors.DIAMOND.color) {
    override val recipes: List<IRecipe>
        get() {
            val recipes = super.recipes.toMutableList()

            recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
                    "iwi", "wsw", "iwi",
                    'w', "gemDiamond",
                    'i', "ingotIron",
                    's', GearStoneItem.oreDictName()
            ))
            recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
                    " w ", "wsw", " w ",
                    'w', "gemDiamond",
                    's', GearIronItem.oreDictName()
            ))

            return recipes.toList()
        }
}
