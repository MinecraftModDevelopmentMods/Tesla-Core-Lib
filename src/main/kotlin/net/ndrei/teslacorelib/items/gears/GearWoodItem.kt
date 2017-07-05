package net.ndrei.teslacorelib.items.gears

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.MATERIAL_WOOD
import net.ndrei.teslacorelib.MaterialColors
import net.ndrei.teslacorelib.annotations.AutoRegisterColoredThingy

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterGear
@AutoRegisterColoredThingy
object GearWoodItem : ColoredGearItem(MATERIAL_WOOD, MaterialColors.WOOD.color) {
    override val recipes: List<IRecipe>
        get() {
            val recipes = super.recipes.toMutableList()

            recipes.add(ShapedOreRecipe(this.registryName, ItemStack(this, 1),
                    " w ",
                    "wsw",
                    " w ",
                    'w', "plankWood",
                    's', "stickWood"
            ))
            recipes.add(ShapedOreRecipe(this.registryName, ItemStack(this, 4),
                    " w ",
                    "wsw",
                    " w ",
                    'w', "logWood",
                    's', "stickWood"
            ))

            return recipes.toList()
        }
}
