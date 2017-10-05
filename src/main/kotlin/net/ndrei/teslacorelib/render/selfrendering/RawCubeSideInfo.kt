package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f

class RawCubeSideInfo(
        var sprite: TextureAtlasSprite? = null,
        var from: Vec2f = Vec2f(0.0f, 0.0f),
        var to: Vec2f = Vec2f(16.0f,16.0f),
        var bothSides: Boolean = false,
        var color: Int = -1) {

    fun autoUV(cube: RawCube, face: EnumFacing) {
        when (face.axis!!) {
            EnumFacing.Axis.X -> {
                from = Vec2f(((Math.min(cube.p1.z, cube.p2.z) / 2.0)).toFloat(), 16.0f - ((Math.min(cube.p1.y, cube.p2.y) / 2.0)).toFloat())
                to = Vec2f(((Math.max(cube.p1.z, cube.p2.z) / 2.0)).toFloat(), 16.0f - ((Math.max(cube.p1.y, cube.p2.y) / 2.0)).toFloat())
            }
            EnumFacing.Axis.Y -> {
                from = Vec2f(((Math.min(cube.p1.x, cube.p2.x) / 2.0)).toFloat(), 16.0f - ((Math.min(cube.p1.z, cube.p2.z) / 2.0)).toFloat())
                to = Vec2f(((Math.max(cube.p1.x, cube.p2.x) / 2.0)).toFloat(), 16.0f - ((Math.max(cube.p1.z, cube.p2.z) / 2.0)).toFloat())
            }
            EnumFacing.Axis.Z -> {
                from = Vec2f(((Math.min(cube.p1.x, cube.p2.x) / 2.0)).toFloat(), 16.0f - ((Math.min(cube.p1.y, cube.p2.y) / 2.0)).toFloat())
                to = Vec2f(((Math.max(cube.p1.x, cube.p2.x) / 2.0)).toFloat(), 16.0f - ((Math.max(cube.p1.y, cube.p2.y) / 2.0)).toFloat())
            }
        }
    }
}
