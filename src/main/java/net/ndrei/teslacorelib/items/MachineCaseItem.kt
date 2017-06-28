package net.ndrei.teslacorelib.items

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.oredict.ShapedOreRecipe
import net.ndrei.teslacorelib.TeslaCoreLib

/**
 * Created by CF on 2017-06-27.
 */
class MachineCaseItem : RegisteredItem(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "machine_case") {
    override val recipe: IRecipe?
        get() = ShapedOreRecipe(null, ItemStack(this, 1),
                "xyx",
                "yzy",
                "xyx",
                'x', "ingotIron",
                'y', "plankWood",
                'z', "blockRedstone"
        )
}