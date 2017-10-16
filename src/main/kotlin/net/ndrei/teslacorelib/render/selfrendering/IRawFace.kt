package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing

interface IRawFace {
    val face: EnumFacing
    var sprite: TextureAtlasSprite?
    var tintIndex: Int

    fun clone(): IRawFace
}
