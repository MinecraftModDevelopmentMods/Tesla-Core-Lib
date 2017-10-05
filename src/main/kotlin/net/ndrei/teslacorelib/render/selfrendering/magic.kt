package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

val AxisAlignedBB.min
    get() = Vec3d(this.minX, this.minY, this.minZ)

val AxisAlignedBB.max
    get() = Vec3d(this.maxX, this.maxY, this.maxZ)

fun IBlockState.getPropertyString()= DefaultStateMapper().getPropertyString(this.properties)

fun Matrix4f.toFloatBuffer(): FloatBuffer {
    val matrixBuf = BufferUtils.createFloatBuffer(16)
    matrixBuf.clear()
    val t = FloatArray(4)
    for (i in 0..3) {
        this.getColumn(i, t)
        matrixBuf.put(t)
    }
    matrixBuf.flip()
    return matrixBuf
}

fun Vec3d.toVector3f() =
    Vector3f(this.x.toFloat(), this.y.toFloat(), this.z.toFloat())

fun Vector3f.toVec3d() =
    Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
