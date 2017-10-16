package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d

interface IRawFigure {
    fun getFaces(): List<IRawFace>
    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null)

    fun clone(sprite: TextureAtlasSprite? = null, reTexture: Boolean = false): IRawFigure
}
