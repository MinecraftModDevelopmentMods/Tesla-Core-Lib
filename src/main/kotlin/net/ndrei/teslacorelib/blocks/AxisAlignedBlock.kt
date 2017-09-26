package net.ndrei.teslacorelib.blocks

import net.minecraft.block.BlockHorizontal
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.ndrei.teslacorelib.getFacingFromEntity

open class AxisAlignedBlock(modId: String, tab: CreativeTabs?, registryName: String, material: Material)
    : RegisteredBlock(modId, tab, registryName, material) {

    init {
        this.defaultState = this.blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        world!!.setBlockState(pos!!, state!!.withProperty(AxisAlignedBlock.FACING, getFacingFromEntity(pos, placer!!)), 2)
        super.onBlockPlacedBy(world, pos, state, placer, stack)
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(AxisAlignedBlock.FACING, state.getValue(AxisAlignedBlock.FACING).rotateY())
            world.setBlockState(pos, state)
            if (tileEntity != null) {
                tileEntity.validate()
                world.setTileEntity(pos, tileEntity)
            }
            return true
        }
        return false
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, AxisAlignedBlock.FACING)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing = EnumFacing.getFront(meta)
        if (enumfacing.axis == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }
        return this.defaultState.withProperty(AxisAlignedBlock.FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(AxisAlignedBlock.FACING).index
    }

    companion object {
        val FACING = BlockHorizontal.FACING!!
    }
}
