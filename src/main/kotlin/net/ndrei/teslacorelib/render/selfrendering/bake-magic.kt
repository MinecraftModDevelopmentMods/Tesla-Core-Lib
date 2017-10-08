package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import net.ndrei.teslacorelib.blocks.multipart.BlockPartHitBox
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

val AxisAlignedBB.min
    get() = Vec3d(this.minX, this.minY, this.minZ)

val AxisAlignedBB.max
    get() = Vec3d(this.maxX, this.maxY, this.maxZ)

fun Pair<Vec3d, Vec3d>.sortCoords() =
    Vec3d(Math.min(this.first.x, this.second.x), Math.min(this.first.y, this.second.y), Math.min(this.first.z, this.second.z)) to
        Vec3d(Math.max(this.first.x, this.second.x), Math.max(this.first.y, this.second.y), Math.max(this.first.z, this.second.z))

fun Pair<Vec3d, Vec3d>.corners(axis: EnumFacing.Axis, size: Double): List<Pair<Vec3d, Vec3d>> {
    val list = mutableListOf<Pair<Vec3d, Vec3d>>()
    val pair = this.sortCoords()
    val diff = pair.second.subtract(pair.first)
    when(axis) {
        EnumFacing.Axis.X -> {
            list.add(pair.first to pair.first.addVector(diff.x, size, size))
            list.add(pair.first.addVector(0.0, 0.0, diff.z - size) to pair.second.addVector(0.0, -diff.y + size, 0.0))
            list.add(pair.second.addVector(-diff.x, -size, -size) to pair.second)
            list.add(pair.first.addVector(0.0, diff.y - size, 0.0) to pair.second.addVector(0.0, 0.0, -diff.z + size))
        }
        EnumFacing.Axis.Y -> {
            list.add(pair.first to pair.first.addVector(size, diff.y, size))
            list.add(pair.first.addVector(0.0, 0.0,diff.z - size) to pair.second.addVector(-diff.x + size, 0.0, 0.0))
            list.add(pair.second.addVector(-size, -diff.y, -size) to pair.second)
            list.add(pair.first.addVector(diff.x - size, 0.0, 0.0) to pair.second.addVector(0.0, 0.0, -diff.z + size))
        }
        EnumFacing.Axis.Z -> {
            list.add(pair.first to pair.first.addVector(size, size, diff.z))
            list.add(pair.first.addVector(diff.x - size, 0.0, 0.0) to pair.second.addVector(0.0, -diff.y + size, 0.0))
            list.add(pair.second.addVector(-size, -size, -diff.z) to pair.second)
            list.add(pair.first.addVector(0.0, diff.y - size, 0.0) to pair.second.addVector(-diff.x + size, 0.0, 0.0))
        }
    }

    var p = Minecraft.getMinecraft().player.position

    return list.map { it.sortCoords() }
}

fun Pair<Vec3d, Vec3d>.chamfers(axis: EnumFacing.Axis, size: Double, chamfer: Float, sprite: TextureAtlasSprite): List<RawLump> {
    val lumps = mutableListOf<RawLump>()

    val corners = this.corners(axis, size)

    lumps.add(RawLump.chamfer(corners[0], axis, EnumFacing.AxisDirection.NEGATIVE, EnumFacing.AxisDirection.NEGATIVE, chamfer, sprite))
    lumps.add(RawLump.chamfer(corners[3], axis, EnumFacing.AxisDirection.NEGATIVE, EnumFacing.AxisDirection.POSITIVE, chamfer, sprite))
    lumps.add(RawLump.chamfer(corners[2], axis, EnumFacing.AxisDirection.POSITIVE, EnumFacing.AxisDirection.POSITIVE, chamfer, sprite))
    lumps.add(RawLump.chamfer(corners[1], axis, EnumFacing.AxisDirection.POSITIVE, EnumFacing.AxisDirection.NEGATIVE, chamfer, sprite))

    return lumps
}

fun EnumFacing.getAxisSizedCoords32(axis: EnumFacing.Axis, padding: Double, size: Double, offset: Double = 0.0) =
    when(this.axis) {
        axis -> when(this.axisDirection) {
            EnumFacing.AxisDirection.NEGATIVE -> offset to size
            EnumFacing.AxisDirection.POSITIVE -> (32.0 - size - offset) to size
        }
        else -> padding to (32.0 - padding * 2.0)
    }

fun EnumFacing.getAxisAlignedAABB32(padding: Double, size: Double, offset: Double = 0.0): AxisAlignedBB {
    val (x, width) = this.getAxisSizedCoords32(EnumFacing.Axis.X, padding, size, offset)
    val (y, height) = this.getAxisSizedCoords32(EnumFacing.Axis.Y, padding, size, offset)
    val (z, depth) = this.getAxisSizedCoords32(EnumFacing.Axis.Z, padding, size, offset)

    return BlockPartHitBox.big32Sized(x, y, z, width, height, depth).aabb
}

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
