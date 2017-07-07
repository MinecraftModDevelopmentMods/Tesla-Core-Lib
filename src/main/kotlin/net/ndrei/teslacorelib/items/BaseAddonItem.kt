package net.ndrei.teslacorelib.items

import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object BaseAddonItem : BaseAddon(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "base_addon") {
//    override val recipe: IRecipe?
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                "xyx",
//                "xxx",
//                "xyx",
//                'x', "paper",
//                'y', "dustRedstone"
//        )
}