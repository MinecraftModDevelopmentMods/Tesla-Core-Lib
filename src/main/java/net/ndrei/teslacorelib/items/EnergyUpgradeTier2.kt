package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object EnergyUpgradeTier2 : EnergyUpgrade(2) {
    override val recipes: List<IRecipe>
        get() = listOf(
                ShapedOreRecipe(null, ItemStack(this, 1),
                        " g ",
                        "rbr",
                        "rrr",
                        'b', BaseAddonItem,
                        'r', "dustRedstone",
                        'g', "gearDiamond"
                ),
                ShapedOreRecipe(null, ItemStack(this, 1),
                        " d ",
                        "dbd",
                        " d ",
                        'b', EnergyUpgradeTier1,
                        'd', "gemDiamond"
                ))
}