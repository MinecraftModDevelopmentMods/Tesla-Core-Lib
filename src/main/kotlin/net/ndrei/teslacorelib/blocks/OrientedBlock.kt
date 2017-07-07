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
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.getFacingFromEntity
import net.ndrei.teslacorelib.render.HudInfoRenderer
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class OrientedBlock<T : TileEntity>
    protected constructor(modId: String, tab: CreativeTabs?, registryName: String, private val teClass: Class<T>, material: Material)
        : RegisteredBlock(modId, tab, registryName, material), ITileEntityProvider {
    protected constructor(modId: String, tab: CreativeTabs, registryName: String, teClass: Class<T>)
        : this(modId, tab, registryName, teClass, Material.ROCK)

    init {
        this.setHarvestLevel("pickaxe", 0)
        this.setHardness(3.0f)

        this.defaultState = this.blockState.baseState
                .withProperty(FACING, EnumFacing.NORTH)
    }

    override fun register(blockRegistry: IForgeRegistry<Block>, itemRegistry: IForgeRegistry<Item>) {
        super.register(blockRegistry, itemRegistry)

        GameRegistry.registerTileEntity(this.teClass, this.registryName!!.toString() + "_tile")
    }

    @SideOnly(Side.CLIENT)
    override fun registerRenderer() {
        super.registerRenderer()

        val renderer = this.specialRenderer
        if (renderer != null) {
            ClientRegistry.bindTileEntitySpecialRenderer(this.teClass, renderer)
        }
    }

    protected open val specialRenderer: TileEntitySpecialRenderer<T>?
        @SideOnly(Side.CLIENT)
        get() = HudInfoRenderer()

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        try {
            return this.teClass.newInstance()
        } catch (e: InstantiationException) {
            TeslaCoreLib.logger.error(e)
            return null
        } catch (e: IllegalAccessException) {
            TeslaCoreLib.logger.error(e)
            return null
        }
    }

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?,
                                  side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val te = world!!.getTileEntity(pos!!)
        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && !world.isRemote) {
            val tank = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
            val bucket = player!!.getHeldItem(hand)
            if (!ItemStackUtil.isEmpty(bucket) && tank != null && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                val handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
                val fluid = handler?.drain(Fluid.BUCKET_VOLUME, false)
                if (fluid != null && fluid.amount == Fluid.BUCKET_VOLUME) {
                    val filled = tank.fill(fluid, false)
                    if (filled == Fluid.BUCKET_VOLUME) {
                        tank.fill(fluid, true)
                        if (!player.capabilities.isCreativeMode) {
                            handler.drain(filled, true)
                            player.setHeldItem(hand, handler.container)
                        }
                    }
                    return true
                }
            }
        }

        if (super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ)) {
            return true
        }

        if (!world.isRemote) {
            player!!.openGui(TeslaCoreLib.instance, 42, world, pos.x, pos.y, pos.z)
        }
        return true
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        world!!.setBlockState(pos!!, state!!.withProperty(FACING, getFacingFromEntity(pos, placer!!)), 2)
        if (!ItemStackUtil.isEmpty(stack) && stack!!.hasTagCompound()) {
            val nbt = stack.tagCompound
            if (nbt != null && nbt.hasKey("tileentity", Constants.NBT.TAG_COMPOUND)) {
                val teNBT = nbt.getCompoundTag("tileentity")
                try {
                    val te = this.createNewTileEntity(world, 0)
                    if (te != null) {
                        te.deserializeNBT(teNBT)
                        world.setTileEntity(pos, te)
                    }
                } catch (t: Throwable) {
                    TeslaCoreLib.logger.error(t)
                }
            }
        }
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var enumfacing = EnumFacing.getFront(meta)
        if (enumfacing.axis == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH
        }
        return this.defaultState.withProperty(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).index
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val t = worldIn.getTileEntity(pos)
        (t as? ElectricTileEntity)?.onBlockBroken()
        super.breakBlock(worldIn, pos, state)
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
        var state = world.getBlockState(pos)
        if (state.block === this) {
            val tileEntity = world.getTileEntity(pos)
            state = state.withProperty(OrientedBlock.FACING, state.getValue(OrientedBlock.FACING).rotateY())
            world.setBlockState(pos, state)
            if (tileEntity != null) {
                tileEntity.validate()
                world.setTileEntity(pos, tileEntity)
            }
            return true
        }
        return false
    }

    companion object {
        val FACING = BlockHorizontal.FACING
    }
}