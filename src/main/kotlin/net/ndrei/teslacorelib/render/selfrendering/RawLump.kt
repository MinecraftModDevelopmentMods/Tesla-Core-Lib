package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d
import javax.vecmath.Vector2d

class RawLump : IRawFigure, IBakery, IBakeable, IDrawable {
    private val faces = mutableListOf<RawLumpFace>()

    override fun getFaces() = this.faces.toList()

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite?, face: EnumFacing, color: Int, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, points.map { color }.toTypedArray(), bothSides))

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite?, face: EnumFacing, colors: Array<Int>? = null, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, colors, bothSides))

    fun addFace(face: RawLumpFace) =
        this.also { it.faces.add(face) }

    fun getRawQauds(transform: TRSRTransformation) =
        mutableListOf<RawQuad>().also { rawrs ->
            this.faces.forEach { it.bake(rawrs, transform) }
        }

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation)
            = mutableListOf<BakedQuad>().also { this.bake(it, vertexFormat, transform) }

    override fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d?) {
        this.getRawQauds(transform).mapTo(quads) {
            it.applyMatrix(matrix ?: (Matrix4d().also { it.setIdentity() })).bake(format)
        }
    }

    override fun draw(buffer: BufferBuilder) {
        this.getRawQauds(TRSRTransformation.identity()).forEach {
            it.draw(buffer)
        }
    }

    companion object {
        private fun Vector2d.pair() = this to this
        private fun Vector2d.offset(x: Double, y: Double) = Vector2d(this.x + x, this.y + y)

        private fun Pair<Pair<Vector2d, Vector2d>, Pair<Vector2d, Vector2d>>.getXFace(x1: Double, x2: Double) = arrayOf(
            Vec3d(x1, this.first.second.y, this.first.second.x),
            Vec3d(x1, this.second.first.y, this.second.first.x),
            Vec3d(x2, this.second.first.y, this.second.first.x),
            Vec3d(x2, this.first.second.y, this.first.second.x)
        )

        private fun Pair<Pair<Vector2d, Vector2d>, Pair<Vector2d, Vector2d>>.getYFace(y1: Double, y2: Double) = arrayOf(
            Vec3d(this.first.second.x, y1, this.first.second.y),
            Vec3d(this.second.first.x, y1, this.second.first.y),
            Vec3d(this.second.first.x, y2, this.second.first.y),
            Vec3d(this.first.second.x, y2, this.first.second.y)
        )

        private fun Pair<Pair<Vector2d, Vector2d>, Pair<Vector2d, Vector2d>>.getZFace(z1: Double, z2: Double) = arrayOf(
            Vec3d(this.first.second.x, this.first.second.y, z1),
            Vec3d(this.second.first.x, this.second.first.y, z1),
            Vec3d(this.second.first.x, this.second.first.y, z2),
            Vec3d(this.first.second.x, this.first.second.y, z2)
        )

        fun chamfer(coords: Pair<Vec3d, Vec3d>, axis: EnumFacing.Axis,
                    dir1: EnumFacing.AxisDirection, dir2: EnumFacing.AxisDirection,
                    size: Float, sprite: TextureAtlasSprite, renderEnds: Boolean = false): RawLump {
            val lump = RawLump()

            val (from, to) = coords.sortCoords()
            val diff = to.subtract(from)

            var c1 = Vector2d(0.0, 0.0).pair()
            var c2 = Vector2d(0.0, 0.0).pair()
            var c3 = Vector2d(0.0, 0.0).pair()
            var c4 = Vector2d(0.0, 0.0).pair()

            var sizeX = 0.0
            var sizeY = 0.0

            when (axis) {
                EnumFacing.Axis.X -> {
                    c1 = Vector2d(from.z, from.y).pair()
                    c2 = Vector2d(from.z, to.y).pair()
                    c3 = Vector2d(to.z, to.y).pair()
                    c4 = Vector2d(to.z, from.y).pair()
                    sizeX = size * diff.z
                    sizeY = size * diff.y
                }
                EnumFacing.Axis.Y -> {
                    c1 = Vector2d(from.x, from.z).pair()
                    c2 = Vector2d(from.x, to.z).pair()
                    c3 = Vector2d(to.x, to.z).pair()
                    c4 = Vector2d(to.x, from.z).pair()
                    sizeX = size * diff.x
                    sizeY = size * diff.z
                }
                EnumFacing.Axis.Z -> {
                    c1 = Vector2d(from.x, from.y).pair()
                    c2 = Vector2d(from.x, to.y).pair()
                    c3 = Vector2d(to.x, to.y).pair()
                    c4 = Vector2d(to.x, from.y).pair()
                    sizeX = size * diff.x
                    sizeY = size * diff.y
                }
            }

            var chamfer = Vector2d(0.0, 0.0).pair()
            when(dir1 to dir2) {
                EnumFacing.AxisDirection.NEGATIVE to EnumFacing.AxisDirection.NEGATIVE -> {
                    c1 = c1.first.offset(sizeX, 0.0) to c1.second.offset(0.0, sizeY)
                    chamfer = c1
                }
                EnumFacing.AxisDirection.NEGATIVE to EnumFacing.AxisDirection.POSITIVE -> {
                    c2 = c2.first.offset(0.0, -sizeY) to c2.second.offset(sizeX, 0.0)
                    chamfer = c2
                }
                EnumFacing.AxisDirection.POSITIVE to EnumFacing.AxisDirection.POSITIVE -> {
                    c3 = c3.first.offset(-sizeX, 0.0) to c3.second.offset(0.0, -sizeY)
                    chamfer = c3
                }
                EnumFacing.AxisDirection.POSITIVE to EnumFacing.AxisDirection.NEGATIVE -> {
                    c4 = c4.first.offset(0.0, sizeY) to c4.second.offset(-sizeX, 0.0)
                    chamfer = c4
                }
            }

            val uvs = arrayOf(
                Vec2f(0.0f, 0.0f),
                Vec2f(0.0f, 1.0f),
                Vec2f(1.0f, 1.0f),
                Vec2f(1.0f, 0.0f)
            )
            when (axis) {
                EnumFacing.Axis.X -> {
                    if (!c1.second.equals(c2.first)) {
                        lump.addFace((c1 to c2).getXFace(from.x, to.x), uvs, sprite, EnumFacing.NORTH)
                    }
                    if (!c2.second.equals(c3.first)) {
                        lump.addFace((c2 to c3).getXFace(from.x, to.x), uvs, sprite, EnumFacing.UP)
                    }
                    if (!c3.second.equals(c4.first)) {
                        lump.addFace((c3 to c4).getXFace(from.x, to.x), uvs, sprite, EnumFacing.SOUTH)
                    }
                    if (!c4.second.equals(c1.first)) {
                        lump.addFace((c4 to c1).getXFace(from.x, to.x), uvs, sprite, EnumFacing.DOWN)
                    }
                    lump.addFace((chamfer to chamfer).getXFace(from.x, to.x).reversedArray(), uvs, sprite, EnumFacing.UP) // TODO: use correct face)
                }
                EnumFacing.Axis.Y -> {
                    if (!c1.second.equals(c2.first)) {
                        lump.addFace((c1 to c2).getYFace(from.y, to.y), uvs, sprite, EnumFacing.WEST)
                    }
                    if (!c2.second.equals(c3.first)) {
                        lump.addFace((c2 to c3).getYFace(from.y, to.y), uvs, sprite, EnumFacing.SOUTH)
                    }
                    if (!c3.second.equals(c4.first)) {
                        lump.addFace((c3 to c4).getYFace(from.y, to.y), uvs, sprite, EnumFacing.EAST)
                    }
                    if (!c4.second.equals(c1.first)) {
                        lump.addFace((c4 to c1).getYFace(from.y, to.y), uvs, sprite, EnumFacing.NORTH)
                    }
                    lump.addFace((chamfer to chamfer).getYFace(from.y, to.y).reversedArray(), uvs, sprite, EnumFacing.UP) // TODO: use correct face)
                }
                EnumFacing.Axis.Z -> {
                    if (!c1.second.equals(c2.first)) {
                        lump.addFace((c1 to c2).getZFace(from.z, to.z).reversedArray(), uvs, sprite, EnumFacing.WEST)
                    }
                    if (!c2.second.equals(c3.first)) {
                        lump.addFace((c2 to c3).getZFace(from.z, to.z).reversedArray(), uvs, sprite, EnumFacing.UP)
                    }
                    if (!c3.second.equals(c4.first)) {
                        lump.addFace((c3 to c4).getZFace(from.z, to.z).reversedArray(), uvs, sprite, EnumFacing.EAST)
                    }
                    if (!c4.second.equals(c1.first)) {
                        lump.addFace((c4 to c1).getZFace(from.z, to.z).reversedArray(), uvs, sprite, EnumFacing.DOWN)
                    }
                    lump.addFace((chamfer to chamfer).getZFace(from.z, to.z), uvs, sprite, EnumFacing.UP) // TODO: use correct face)
                }
            }

            return lump
        }
    }

    override fun clone(sprite: TextureAtlasSprite?, reTexture: Boolean) = RawLump().also {
        this.faces.forEach { face ->
            val f = face.clone()
            if ((sprite != null) || reTexture) {
                f.sprite = sprite
            }
            it.addFace(f)
        }
    }
}
