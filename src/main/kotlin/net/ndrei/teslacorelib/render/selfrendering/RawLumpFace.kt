package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation

class RawLumpFace(points: Array<Vec3d>, uvs: Array<Vec2f>, override var sprite: TextureAtlasSprite?, override val face: EnumFacing, colors: Array<Int>? = null, private val bothSides: Boolean = false)
    : IRawFace {

    private val points: Array<Vec3d>
    private val uvs: Array<Vec2f>
    private val colors: Array<Int>

    override var tintIndex = -1

    init {
        if ((points.size != 3) && (points.size != 4)) {
            throw IllegalArgumentException("Only accepting faces with 3 or 4 points.")
        }

        if ((uvs.size != 3) && (uvs.size != 4)) {
            throw IllegalArgumentException("Only accepting faces with 3 or 4 texture points.")

        // TODO: add a test for points == 3 and uvs == 3

        this.points = if (points.size == 3) { arrayOf(points[0], points[1], points[2], points[0]) } else points
        this.uvs = if (uvs.size == 3) { arrayOf(uvs[0], uvs[1], uvs[2], uvs[0]) } else uvs

        val rawColors = if ((colors != null) && (colors.size != points.size)) {
            throw IllegalArgumentException("Number of points must be the same as the number of colors.")
        }
        else if (colors != null) { colors }
        else { points.map { -1 }.toTypedArray() }
        this.colors = if (rawColors.size == 3) { arrayOf(rawColors[0], rawColors[1], rawColors[2], rawColors[0]) } else rawColors
    }

    fun bake(rawrs: MutableList<RawQuad>, transform: TRSRTransformation) {
        rawrs.add(RawQuad(
            this.points[0], this.uvs[0].x, this.uvs[0].y,
            this.points[1], this.uvs[1].x, this.uvs[1].y,
            this.points[2], this.uvs[2].x, this.uvs[2].y,
            this.points[3], this.uvs[3].x, this.uvs[3].y,
            this.face,
            this.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite,
            this.colors[0], // TODO: implement different colors for each point
            transform, this.tintIndex)
        )
        if (this.bothSides) {
            rawrs.add(RawQuad(
                this.points[0], this.uvs[0].x, this.uvs[0].y,
                this.points[3], this.uvs[3].x, this.uvs[3].y,
                this.points[2], this.uvs[2].x, this.uvs[2].y,
                this.points[1], this.uvs[1].x, this.uvs[1].y,
                this.face.opposite,
                this.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite,
                this.colors[0], // TODO: implement different colors for each point
                transform, this.tintIndex)
            )
        }
    }

    override fun clone() = RawLumpFace(this.points, this.uvs, this.sprite, this.face, this.colors, this.bothSides).also { it.tintIndex = this.tintIndex }
}