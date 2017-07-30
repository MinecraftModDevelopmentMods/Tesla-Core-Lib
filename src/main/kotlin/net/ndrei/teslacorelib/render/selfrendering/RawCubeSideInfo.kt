package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.math.Vec2f

class RawCubeSideInfo(
        var sprite: TextureAtlasSprite? = null,
        var from: Vec2f = Vec2f(0.0f, 0.0f),
        var to: Vec2f = Vec2f(16.0f,16.0f),
        var bothSides: Boolean = false,
        var color: Int = -1) {

    fun autoUV(cube: RawCube?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
