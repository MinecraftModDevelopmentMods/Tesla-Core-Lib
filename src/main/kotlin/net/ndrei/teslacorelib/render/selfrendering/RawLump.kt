package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d

class RawLump : IRawFigure, IBakery {
    private val faces = mutableListOf<RawLumpFace>()

    override fun getFaces() = this.faces.toList()

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite?, face: EnumFacing, color: Int, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, points.map { color }.toTypedArray(), bothSides))

    fun addFace(points: Array<Vec3d>, uvs: Array<Vec2f>, sprite: TextureAtlasSprite?, face: EnumFacing, colors: Array<Int>? = null, bothSides: Boolean = false) =
        addFace(RawLumpFace(points, uvs, sprite, face, colors, bothSides))

    fun addFace(face: RawLumpFace) =
        this.also { it.faces.add(face) }

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation)
        = mutableListOf<BakedQuad>().also { this.bake(it, vertexFormat, transform) }

    override fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d?) {
        val rawrs = mutableListOf<RawQuad>()

        this.faces.forEach { it.bake(rawrs, transform) }

        rawrs.mapTo(quads) { it.applyMatrix(matrix ?: (Matrix4d().also { it.setIdentity() })).bake(format) }
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
