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

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
