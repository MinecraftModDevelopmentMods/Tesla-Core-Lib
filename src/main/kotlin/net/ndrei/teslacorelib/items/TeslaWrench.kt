package net.ndrei.teslacorelib.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object TeslaWrench : CoreItem("wrench") {
    init {
        super.setMaxStackSize(1)
    }

//    override val recipe: IRecipe?
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                " LX",
//                " XR",
//                "X  ",
//                'X', "ingotIron",
//                'R', "dustRedstone",
//                'L', "dyeBlue"
//        )

    override fun onItemUseFirst(player: EntityPlayer?, worldIn: World?, pos: BlockPos?,
                                facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float,
                                hand: EnumHand?): EnumActionResult {
        var result = EnumActionResult.PASS

        // test if block implements the interface
        val state = worldIn!!.getBlockState(pos!!)
        if (state.block is ITeslaWrenchHandler) {
            result = (state.block as ITeslaWrenchHandler).onWrenchUse(this,
                    player!!, worldIn, pos, hand!!, facing!!, hitX, hitY, hitZ)
        }

        if (result == EnumActionResult.PASS) {
            // test if entity has the capability
            val entity = worldIn.getTileEntity(pos)
            if (entity != null && entity.hasCapability(TeslaCoreCapabilities.CAPABILITY_WRENCH, facing)) {
                result = entity.getCapability(TeslaCoreCapabilities.CAPABILITY_WRENCH, facing)!!.onWrenchUse(this,
                        player!!, worldIn, pos, hand!!, facing!!, hitX, hitY, hitZ)
            }
        }

        return result
    }
}