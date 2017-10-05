package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d

class RawLump {
    private val faces = mutableListOf<RawLumpFace>()

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite, face: EnumFacing, color: Int, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, points.map { color }.toTypedArray(), bothSides))

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite, face: EnumFacing, colors: Array<Int>? = null, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, colors, bothSides))

    fun addFace(face: RawLumpFace) =
        this.also { it.faces.add(face) }

    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null) {
        val rawrs = mutableListOf<RawQuad>()

        this.faces.forEach { it.bake(rawrs, transform) }

        rawrs.mapTo(quads) { it.applyMatrix(matrix ?: (Matrix4d().also { it.setIdentity() })).bake(format) }
    }
}
