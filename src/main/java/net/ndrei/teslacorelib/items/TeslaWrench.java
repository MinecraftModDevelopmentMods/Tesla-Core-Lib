package net.ndrei.teslacorelib.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler;

/**
 * Created by CF on 2016-12-13.
 */
public class TeslaWrench extends RegisteredItem {
    public TeslaWrench() {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "wrench");
        super.setMaxStackSize(1);
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                " LX", " XR", "X  ",
                'X', Items.IRON_INGOT,
                'R', Items.REDSTONE,
                'L', new ItemStack(Items.DYE, 1, 4));
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos,
                                           EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        EnumActionResult result = EnumActionResult.PASS;

        // test if block implements the interface
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof ITeslaWrenchHandler) {
            result = ((ITeslaWrenchHandler)state.getBlock()).onWrenchUse(this,
                    player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }

        if (result == EnumActionResult.PASS) {
            // test if entity has the capability
            TileEntity entity = worldIn.getTileEntity(pos);
            if ((entity != null) && (entity.hasCapability(TeslaCoreCapabilities.CAPABILITY_WRENCH, facing))) {
                result = entity.getCapability(TeslaCoreCapabilities.CAPABILITY_WRENCH, facing).onWrenchUse(this,
                        player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            }
        }

        return result;
    }
}
