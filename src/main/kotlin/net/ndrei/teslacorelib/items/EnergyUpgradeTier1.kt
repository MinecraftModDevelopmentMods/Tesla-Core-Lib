package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object EnergyUpgradeTier1 : EnergyUpgrade(1) {
    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                " g ",
                "rbr",
                "rrr",
                'b', BaseAddonItem,
                'r', "dustRedstone",
                'g', "gearGold"
        )
}