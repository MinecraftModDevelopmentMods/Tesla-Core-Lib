package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.texture.TextureAtlasSprite

interface IRawFigure : IBakeable {
    fun getFaces(): List<IRawFace>
    // fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null)

    fun clone(sprite: TextureAtlasSprite? = null, reTexture: Boolean = false): IRawFigure
}
