package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation

class StaticBakery(val bakery: IBakery) : IBakery {
    private var quads: MutableList<BakedQuad>? = null

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
        if (this.quads == null) {
            this.quads = this.bakery.getQuads(state, stack, side, vertexFormat, transform)
        }
        return this.quads!!
    }
}