package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterItem
object GearWoodItem : BaseGearItem("wood") {
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
