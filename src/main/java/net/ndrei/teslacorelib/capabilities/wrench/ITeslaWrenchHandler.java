package net.ndrei.teslacorelib.capabilities.wrench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ndrei.teslacorelib.items.TeslaWrench;

/**
 * Created by CF on 2016-12-13.
 */
public interface ITeslaWrenchHandler {
    EnumActionResult onWrenchUse(TeslaWrench wrench,
                                 EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
                                 EnumFacing facing, float hitX, float hitY, float hitZ);
}
