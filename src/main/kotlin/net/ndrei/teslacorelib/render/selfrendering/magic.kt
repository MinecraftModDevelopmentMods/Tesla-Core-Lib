package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d

val AxisAlignedBB.min
    get() = Vec3d(this.minX, this.minY, this.minZ)

val AxisAlignedBB.max
    get() = Vec3d(this.maxX, this.maxY, this.maxZ)