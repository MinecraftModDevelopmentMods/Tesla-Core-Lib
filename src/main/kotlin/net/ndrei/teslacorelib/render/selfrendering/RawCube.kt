package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d
import javax.vecmath.Vector3f

class RawCube(val p1: Vec3d, val p2: Vec3d, val sprite: TextureAtlasSprite? = null)
    : IBakery {
    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation)
            = mutableListOf<BakedQuad>().also { this.bake(it, vertexFormat, transform) }

    private val map = mutableMapOf<EnumFacing, RawCubeSideInfo>()
    private var lastSide: EnumFacing? = null
    private var autoUVFlag = false
    private var dualSideFlag = false

    fun addFace(face: EnumFacing) = this.also {
        this.map[face] = RawCubeSideInfo()
        this.lastSide = face

        this.getLastSideInfo().also {
            if (this.autoUVFlag) {
                it.autoUV(this, face)
            }
            it.bothSides = this.dualSideFlag
        }
    }

    private fun getLastSideInfo()
            = (if (this.lastSide != null) this.map[this.lastSide!!] else null)
            ?: throw Exception("No side created yet!")

    fun sprite(sprite: TextureAtlasSprite) = this.also {
        this.getLastSideInfo().sprite = sprite
    }

    fun autoUV(flag: Boolean = true) = this.also {
        if (this.lastSide != null) {
            throw Exception("You can only set this before creating any sides!")
            // this.getLastSideInfo().autoUV(if (flag) this else null)
        }
//        else {
        this.autoUVFlag = flag
//        }
    }

    fun uv(u1: Float, v1: Float, u2: Float, v2: Float) = this.uv(Vec2f(u1, v1), Vec2f(u2, v2))

    fun uv(t1: Vec2f, t2: Vec2f) = this.also {
        this.getLastSideInfo().also {
            it.from = t1
            it.to = t2
        }
    }

    fun color(color: Int) = this.also {
        this.getLastSideInfo().color = color
    }

    fun dualSide(flag: Boolean = true) = this.also {
        if (this.lastSide != null) {
            this.getLastSideInfo().bothSides = flag
        } else {
            this.dualSideFlag = flag
        }
    }

    fun addMissingFaces(): RawCube {
        EnumFacing.VALUES.forEach {
            if (!this.map.containsKey(it))
                this.addFace(it)
        }
        return this
    }

    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null) {
        val rawrs = mutableListOf<RawQuad>()

        val p1 = Vector3f(this.p1.x.toFloat(), this.p1.y.toFloat(), this.p1.z.toFloat())
        val p2 = Vector3f(this.p2.x.toFloat(), this.p2.y.toFloat(), this.p2.z.toFloat())
//        transform.matrix.transform(p1)
//        transform.matrix.transform(p2)

        // order coords TODO: maybe not needed?
        val (x1, x2) = (if (p1.x < p2.x) Pair(p1.x, p2.x) else Pair(p2.x, p1.x)) // .let { Pair(it.first.toDouble(), it.second.toDouble()) }
        val (y1, y2) = (if (p1.y < p2.y) Pair(p1.y, p2.y) else Pair(p2.y, p1.y)) // .let { Pair(it.first.toDouble(), it.second.toDouble()) }
        val (z1, z2) = (if (p1.z < p2.z) Pair(p1.z, p2.z) else Pair(p2.z, p1.z)) // .let { Pair(it.first.toDouble(), it.second.toDouble()) }

        this.map.forEach { face, info ->
            val sprite = info.sprite ?: this.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
            val tface = face // transform.rotate(face)
            val v1 = Vector3f(x1, y1, z1)
            val v2 = Vector3f(x2, y2, z2)
//            transform.matrix.transform(v1)
//            transform.matrix.transform(v2)

            if (info.bothSides) {
                rawrs.addDoubleFace(tface, sprite, info.color,
                        Vec3d(v1.x.toDouble(), v1.y.toDouble(), v1.z.toDouble()),
                        Vec3d(v2.x.toDouble(), v2.y.toDouble(), v2.z.toDouble()),
                        info.from, info.to, transform)
            }
            else {
                rawrs.addSingleFace(tface, sprite, info.color,
                        Vec3d(v1.x.toDouble(), v1.y.toDouble(), v1.z.toDouble()),
                        Vec3d(v2.x.toDouble(), v2.y.toDouble(), v2.z.toDouble()),
                        info.from, info.to, transform)
            }
        }

        rawrs.mapTo(quads) { it.applyMatrix(matrix ?: Matrix4d().also { it.setIdentity() }).bake(format) }
    }
}
