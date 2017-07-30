package net.ndrei.teslacorelib.render.selfrendering

import com.google.common.cache.CacheBuilder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.model.TRSRTransformation
import java.util.concurrent.TimeUnit

class CachedBakery(val bakery: IBakery) : IBakery {
    constructor(bakery: (state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation) -> MutableList<BakedQuad>)
    : this(object: IBakery {
        override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation)
                = bakery(state, stack, side, vertexFormat, transform)
    })

    private val cache = CacheBuilder.newBuilder().expireAfterAccess(42, TimeUnit.SECONDS).build<String, MutableList<BakedQuad>>()

    var keyGetter: (state: IBlockState?, stack: ItemStack?, side: EnumFacing?) -> String
        = { state , stack, side -> "${state?.toString() ?: "no-state"}::${stack?.toString() ?: "no-stack"}::${side?.toString() ?: "no-side"}" }

    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat, transform: TRSRTransformation): MutableList<BakedQuad> {
        val key = this.keyGetter(state, stack, side)
        return cache.get(key) {
            // TeslaThingiesMod.logger.info("Generating cached baked quads: '$key'.")
            this.bakery.getQuads(state, stack, side, vertexFormat, transform)
        }
    }
}
