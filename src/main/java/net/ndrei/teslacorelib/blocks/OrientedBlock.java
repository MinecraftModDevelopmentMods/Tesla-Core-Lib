package net.ndrei.teslacorelib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.Utils;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.render.HudInfoRenderer;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;

/**
 * Created by CF on 2016-12-03.
 */
public class OrientedBlock<T extends TileEntity> extends Block implements ITileEntityProvider {
    @SuppressWarnings("WeakerAccess")
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private Class<T> teClass;

    @SuppressWarnings("unused")
    protected OrientedBlock(String modId, CreativeTabs tab, String registryName, Class<T> teClass) {
        this(modId, tab, registryName, teClass, Material.ROCK);
    }

    @SuppressWarnings("WeakerAccess")
    protected OrientedBlock(String modId, CreativeTabs tab, String registryName, Class<T> teClass, Material material) {
        super(material);
        this.teClass = teClass;

        this.setRegistryName(modId, registryName);
        this.setUnlocalizedName(modId + "_" + registryName);
        if (tab != null) {
            this.setCreativeTab(tab);
        }
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
                                    ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if ((te != null) && (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) && !world.isRemote) {
            IFluidHandler tank = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            ItemStack bucket = player.getHeldItem(hand);
            if (!ItemStackUtil.isEmpty(bucket) && (tank != null) && (bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))) {
                IFluidHandler handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                FluidStack fluid = (handler != null) ? handler.drain(Fluid.BUCKET_VOLUME, false) : null;
                if ((fluid != null) && (fluid.amount == Fluid.BUCKET_VOLUME)) {
                    int filled = tank.fill(fluid, false);
                    if (filled == Fluid.BUCKET_VOLUME) {
                        tank.fill(fluid, true);
                        if (!player.capabilities.isCreativeMode) {
                            handler.drain(filled, true);
                        }
                    }
                    return true;
                }
            }
        }

        if (super.onBlockActivated(world, pos, state, player, hand, held, side, hitX, hitY, hitZ)) {
            return true;
        }

        if (!world.isRemote) {
            player.openGui(TeslaCoreLib.instance, 42, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
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
        if (t instanceof ElectricTileEntity) {
            ((ElectricTileEntity) t).onBlockBroken();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this) {
            TileEntity tileEntity = world.getTileEntity(pos);
            state = state.withProperty(OrientedBlock.FACING, state.getValue(OrientedBlock.FACING).rotateY());
            world.setBlockState(pos, state);
            if (tileEntity != null) {
                tileEntity.validate();
                world.setTileEntity(pos, tileEntity);
            }
            return true;
        }
        return false;
    }
}
