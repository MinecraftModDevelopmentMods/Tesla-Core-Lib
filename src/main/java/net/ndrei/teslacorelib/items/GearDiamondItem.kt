package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterItem
object GearDiamondItem : BaseGearItem("diamond") {
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
