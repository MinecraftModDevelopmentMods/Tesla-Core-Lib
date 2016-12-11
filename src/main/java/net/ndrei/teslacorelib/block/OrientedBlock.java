package net.ndrei.teslacorelib.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.Utils;
import net.ndrei.teslacorelib.render.HudInfoRenderer;

/**
 * Created by CF on 2016-12-03.
 */
public class OrientedBlock<T extends TileEntity> extends Block implements ITileEntityProvider {
    @SuppressWarnings("WeakerAccess")
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private Class<T> teClass;

    @SuppressWarnings("unused")
    protected OrientedBlock(String blockId, Class<T> teClass) {
        this(blockId, teClass, Material.ROCK);
    }

    @SuppressWarnings("WeakerAccess")
    protected OrientedBlock(String blockId, Class<T> teClass, Material material) {
        super(material);
        this.teClass = teClass;

        this.setRegistryName(blockId);

        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(3.0f);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH));
    }

    @SuppressWarnings("unused")
    public void register() {
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), this.getRegistryName());
        GameRegistry.registerTileEntity(this.teClass, this.getRegistryName() + "_tile");

        IRecipe recipe = this.getRecipe();
        if (recipe != null) {
            CraftingManager.getInstance().addRecipe(recipe);
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected IRecipe getRecipe() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unused")
    public void registerRenderer() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this)
                , 0
                , new ModelResourceLocation(this.getRegistryName(), "inventory")
        );

        TileEntitySpecialRenderer<T> renderer = this.getSpecialRenderer();
        if (renderer != null) {
            ClientRegistry.bindTileEntitySpecialRenderer(this.teClass, renderer);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @SideOnly(Side.CLIENT)
    protected TileEntitySpecialRenderer<T> getSpecialRenderer() {
        return new HudInfoRenderer<>();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return this.teClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            TeslaCoreLib.logger.error(e);
            return null;
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if ((te != null) && (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))) {
            IFluidHandler tank = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            ItemStack bucket = player.getHeldItem(hand);
            if (!bucket.isEmpty() && (tank != null) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))) {
                IFluidHandler handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                FluidStack fluid = (handler != null) ? handler.drain(1000, false) : null;
                if ((fluid != null) && (fluid.amount > 0)) {
                    int filled = tank.fill(fluid, false);
                    if (filled == fluid.amount) {
                        tank.fill(fluid, true);
                        if (!player.capabilities.isCreativeMode) {
                            handler.drain(filled, true);
                        }
                    }
                    return true;
                }
            }
        }

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        world.setBlockState(pos, state.withProperty(FACING, Utils.getFacingFromEntity(pos, placer)), 2);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) { enumfacing = EnumFacing.NORTH; }
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity t = worldIn.getTileEntity(pos);
        if ((t != null) && (t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))) {
            IItemHandler handler = t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty() && (stack.getCount() > 0)) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state);
    }
}
