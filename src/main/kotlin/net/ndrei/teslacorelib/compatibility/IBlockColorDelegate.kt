package net.ndrei.teslacorelib.compatibility

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Created by CF on 2017-07-09.
 */
interface IBlockColorDelegate {
    fun colorMultiplier(state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int): Int
}
