package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.block.BlockHorizontal
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.getFacingFromEntity
import net.ndrei.teslacorelib.render.SidedTileEntityRenderer
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class OrientedBlock<T : SidedTileEntity>
    protected constructor(modId: String, tab: CreativeTabs?, registryName: String, private val teClass: Class<T>, material: Material)
        : AxisAlignedBlock(modId, tab, registryName, material), ITileEntityProvider {
    protected constructor(modId: String, tab: CreativeTabs, registryName: String, teClass: Class<T>)
        : this(modId, tab, registryName, teClass, Material.ROCK)

    init {
        this.setHarvestLevel("pickaxe", 0)
        this.setHardness(3.0f)
//
//        this.defaultState = this.blockState.baseState
//                .withProperty(FACING, EnumFacing.NORTH)
    }

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        super.registerBlock(registry)
        GameRegistry.registerTileEntity(this.teClass, this.registryName!!.toString() + "_tile")
    }

    @SideOnly(Side.CLIENT)
    override fun registerRenderer() {
        super.registerRenderer()

        ClientRegistry.bindTileEntitySpecialRenderer(this.teClass, this.specialRenderer)
    }

    @Deprecated("One should not override this.", ReplaceWith("One should use the SidedTileEntity.getRenderers!"), DeprecationLevel.WARNING)
    protected open val specialRenderer: TileEntitySpecialRenderer<SidedTileEntity>
        @SideOnly(Side.CLIENT)
        get() = SidedTileEntityRenderer

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return try {
            this.teClass.newInstance()
        } catch (e: InstantiationException) {
            TeslaCoreLib.logger.error(e)
            null
        } catch (e: IllegalAccessException) {
            TeslaCoreLib.logger.error(e)
            null
        }
    }

//    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?,
//                                  facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
//        if ((worldIn != null) && !worldIn.isRemote && (pos != null) && (playerIn != null) && (hand != null) && (facing != null)) {
//            val te = worldIn.getTileEntity(pos) as? SidedTileEntity
//            val bucket = playerIn.getHeldItem(hand)
//            if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
//                if ((te != null) && te.handleBucket(playerIn, hand/*, side*/)) {
//                    return true
//                }
//            }
//        }
//
//        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
//    }
//    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
//        world!!.setBlockState(pos!!, state!!.withProperty(FACING, getFacingFromEntity(pos, placer!!)), 2)
//        super.onBlockPlacedBy(world, pos, state, placer, stack)
//    }
//
//    override fun createBlockState(): BlockStateContainer {
//        return BlockStateContainer(this, FACING)
//    }
//
//    override fun getStateFromMeta(meta: Int): IBlockState {
//        var enumfacing = EnumFacing.getFront(meta)
//        if (enumfacing.axis == EnumFacing.Axis.Y) {
//            enumfacing = EnumFacing.NORTH
//        }
//        return this.defaultState.withProperty(FACING, enumfacing)
//    }
//
//    override fun getMetaFromState(state: IBlockState): Int {
//        return state.getValue(FACING).index
//    }
//
//    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
//        var state = world.getBlockState(pos)
//        if (state.block === this) {
//            val tileEntity = world.getTileEntity(pos)
//            state = state.withProperty(OrientedBlock.FACING, state.getValue(OrientedBlock.FACING).rotateY())
//            world.setBlockState(pos, state)
//            if (tileEntity != null) {
//                tileEntity.validate()
//                world.setTileEntity(pos, tileEntity)
//            }
//            return true
//        }
//        return false
//    }

//    companion object {
//        val FACING = BlockHorizontal.FACING!!
//    }
}
