package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.render.ISelfRegisteringRenderer
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-07-07.
 */
abstract class RegisteredBlock(modId: String, tab: CreativeTabs?, registryName: String, material: Material)
    : Block(material), ISelfRegisteringBlock, ISelfRegisteringRenderer {
    init {
        this.setRegistryName(modId, registryName)
        this.translationKey = "$modId.$registryName"
        if (tab != null) {
            this.creativeTab = tab
        }
    }

    //#region registration methods

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        registry.register(this)
    }

    override fun registerItem(registry: IForgeRegistry<Item>) {
        val item = ItemBlock(this)
        item.registryName = this.registryName
        registry.register(item)
    }

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    open fun registerRecipe(registry: (recipe: IRecipe) -> ResourceLocation) = this.recipes.forEach { registry(it) }

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    protected open val recipe: IRecipe?
        get() = null

    @Deprecated("One should really use JSON resources for recipes.", ReplaceWith("A JSON File!"), DeprecationLevel.WARNING)
    protected open val recipes: List<IRecipe>
        get() {
            val recipe = this.recipe
            return if (recipe != null) listOf(recipe) else listOf()
        }

    @SideOnly(Side.CLIENT)
    override fun registerRenderer() {
        this.registerItemBlockRenderer()
    }

    @SideOnly(Side.CLIENT)
    protected open fun registerItemBlockRenderer() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
                ModelResourceLocation(this.registryName!!, "inventory")
        )
    }

    //#endregion

    open fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? = null

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if ((worldIn != null) && !worldIn.isRemote && (pos != null) && (playerIn != null) && (hand != null) && (facing != null)) {
            val te = worldIn.getTileEntity(pos) as? SidedTileEntity
            val bucket = playerIn.getHeldItem(hand)
            if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                if ((te != null) && te.handleBucket(playerIn, hand/*, side*/)) {
                    return true
                }
            }
        }

        if (super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)) {
            return true
        }

        if ((worldIn != null) && (pos != null) && !worldIn.isRemote) {
            val te = worldIn.getTileEntity(pos)
            if ((te != null) && te.hasCapability(TeslaCoreCapabilities.CAPABILITY_GUI_CONTAINER, null)) {
                playerIn!!.openGui(TeslaCoreLib, 42, worldIn, pos.x, pos.y, pos.z)
            }
        }
        return true
    }

    override fun onBlockPlacedBy(world: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        if ((stack != null) && !stack.isEmpty && stack.hasTagCompound() && (this is ITileEntityProvider) && (world != null)) {
            val nbt = stack.tagCompound
            if (nbt != null && nbt.hasKey("tileentity", Constants.NBT.TAG_COMPOUND)) {
                val teNBT = nbt.getCompoundTag("tileentity")
                val teMeta = if (nbt.hasKey("te_blockstate_meta", Constants.NBT.TAG_INT)) nbt.getInteger("te_blockstate_meta") else 0
                try {
                    val te = this.createNewTileEntity(world, teMeta)
                    if (te != null) {
                        te.deserializeNBT(teNBT)
                        world.setTileEntity(pos, te)
                    }
                } catch (t: Throwable) {
                    TeslaCoreLib.logger.error(t)
                }
            }
        }
        super.onBlockPlacedBy(world, pos, state, placer, stack)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        (worldIn.getTileEntity(pos) as? SidedTileEntity)?.onBlockBroken()
        super.breakBlock(worldIn, pos, state)
    }
}
