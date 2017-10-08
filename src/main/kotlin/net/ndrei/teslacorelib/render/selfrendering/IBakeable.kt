package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4d

@FunctionalInterface
interface IBakeable {
    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat, transform: TRSRTransformation, matrix: Matrix4d? = null)
}