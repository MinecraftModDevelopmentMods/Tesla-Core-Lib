package net.ndrei.teslacorelib

import net.minecraft.entity.Entity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by CF on 2017-06-28.
 */
fun getFacingFromEntity(pos: BlockPos, entity: Entity): EnumFacing {
    return getFacingFromEntity(pos, entity.posX, entity.posZ)
}

fun getFacingFromEntity(pos: BlockPos, entityX: Double, entityZ: Double): EnumFacing {
    return EnumFacing.getFacingFromVector((entityX - pos.x).toFloat(), 0f, (entityZ - pos.z).toFloat())
}
