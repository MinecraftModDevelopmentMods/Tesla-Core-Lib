package net.ndrei.teslacorelib;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created by CF on 2016-12-03.
 */
public final class Utils {
    public static EnumFacing getFacingFromEntity(BlockPos pos, Entity entity) {
        return Utils.getFacingFromEntity(pos, entity.posX, entity.posZ);
    }

    public static EnumFacing getFacingFromEntity(BlockPos pos, double entityX, double entityZ) {
        return EnumFacing.getFacingFromVector((float) (entityX - pos.getX()), 0, (float) (entityZ - pos.getZ()));
    }
}
