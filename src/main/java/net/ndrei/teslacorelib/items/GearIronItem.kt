package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-22.
 */
@AutoRegisterItem
object GearIronItem : BaseGearItem("iron") {
    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                " w ",
                "wsw",
                " w ",
                'w', "ingotIron",
                's', GearStoneItem.oreDictName()
        )
}
