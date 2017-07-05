package net.ndrei.teslacorelib.items.gears

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.MATERIAL_GOLD
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
@Suppress("unused")
object GearGoldItem : ColoredGearItem(MATERIAL_GOLD, MaterialColors.GOLD.color) {
    override val recipes: List<IRecipe>
        get() {
            val recipes = super.recipes.toMutableList()

            recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
                    "iwi",
                    "wsw",
                    "iwi",
                    'w', "ingotGold",
                    'i', "ingotIron",
                    's', GearStoneItem.oreDictName()
            ))
            recipes.add(ShapedOreRecipe(null, ItemStack(this, 1),
                    " w ",
                    "wsw",
                    " w ",
                    'w', "ingotGold",
                    's', GearIronItem.oreDictName()
            ))

            return recipes.toList()
        }
}
