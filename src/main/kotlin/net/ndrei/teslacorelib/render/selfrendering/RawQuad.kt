package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d
import javax.vecmath.Vector4d

@Suppress("MemberVisibilityCanPrivate")
class RawQuad(
        val p1: Vec3d, val u1: Float, val v1: Float,
        val p2: Vec3d, val u2: Float, val v2: Float,
        val p3: Vec3d, val u3: Float, val v3: Float,
        val p4: Vec3d, val u4: Float, val v4: Float,
        val side: EnumFacing, val sprite: TextureAtlasSprite, val color: Int,
        val transform: TRSRTransformation?, val tintIndex: Int = -1) {

    private fun Float.u() = this@RawQuad.sprite.getInterpolatedU(this.toDouble()/* / 16.0 * sprite.iconWidth*/)
    private fun Float.v() = this@RawQuad.sprite.getInterpolatedV(this.toDouble()/* / 16.0 * sprite.iconHeight*/)

    private fun Vec3d.toVector4d() = Vector4d(this.x, this.y, this.z, 1.0)
    private fun Vector4d.toVec3d() = Vec3d(this.x, this.y, this.z)

    fun applyMatrix(matrix: Matrix4d): RawQuad {
        val p1 = this.p1.toVector4d().also { matrix.transform(it) }.toVec3d()
        val p2 = this.p2.toVector4d().also { matrix.transform(it) }.toVec3d()
        val p3 = this.p3.toVector4d().also { matrix.transform(it) }.toVec3d()
        val p4 = this.p4.toVector4d().also { matrix.transform(it) }.toVec3d()
        return RawQuad(p1, this.u1, this.v1,
            p2, this.u2, this.v2,
            p3, this.u3, this.v3,
            p4, this.u4, this.v4,
            this.side, this.sprite, this.color, this.transform, this.tintIndex)
    }

    fun bake(format: VertexFormat): BakedQuad {
        val normal = Vec3d(side.xOffset.toDouble(), side.yOffset.toDouble(), side.zOffset.toDouble())

        val builder = UnpackedBakedQuad.Builder(format)
        builder.setTexture(this.sprite)
        builder.setQuadOrientation(this.side)
        builder.setQuadTint(this.tintIndex)
        builder.putVertex(this.sprite, normal, this.p1.x / 32.0, this.p1.y / 32.0, this.p1.z / 32.0, this.u1.u(), this.v1.v(), this.color, this.transform)
        builder.putVertex(this.sprite, normal, this.p2.x / 32.0, this.p2.y / 32.0, this.p2.z / 32.0, this.u2.u(), this.v2.v(), this.color, this.transform)
        builder.putVertex(this.sprite, normal, this.p3.x / 32.0, this.p3.y / 32.0, this.p3.z / 32.0, this.u3.u(), this.v3.v(), this.color, this.transform)
        builder.putVertex(this.sprite, normal, this.p4.x / 32.0, this.p4.y / 32.0, this.p4.z / 32.0, this.u4.u(), this.v4.v(), this.color, this.transform)
        return builder.build()
    }

    fun draw(buffer: BufferBuilder) {
        val b = this.color and 255
        val g = (this.color shr 8) and 255
        val r = (this.color shr 16) and 255
        val a = (this.color ushr 24) and 255

        buffer.pos(this.p1.x, this.p1.y, this.p1.z).tex(this.u1.u().toDouble(), this.v1.v().toDouble()).color(r, g, b, a).endVertex()
        buffer.pos(this.p2.x, this.p2.y, this.p2.z).tex(this.u2.u().toDouble(), this.v2.v().toDouble()).color(r, g, b, a).endVertex()
        buffer.pos(this.p3.x, this.p3.y, this.p3.z).tex(this.u3.u().toDouble(), this.v3.v().toDouble()).color(r, g, b, a).endVertex()
        buffer.pos(this.p4.x, this.p4.y, this.p4.z).tex(this.u4.u().toDouble(), this.v4.v().toDouble()).color(r, g, b, a).endVertex()
    }
}
