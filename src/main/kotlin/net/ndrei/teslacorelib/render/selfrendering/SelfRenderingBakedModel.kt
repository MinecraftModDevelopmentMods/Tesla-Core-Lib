package net.ndrei.teslacorelib.render.selfrendering

import com.google.common.cache.CacheBuilder
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import java.util.concurrent.TimeUnit
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

open class SelfRenderingBakedModel(val renderer: ISelfRenderingBlock, val format: VertexFormat, val transform: TRSRTransformation? = null) : IBakedModel {
    override fun getParticleTexture(): TextureAtlasSprite {
        val rl = this.renderer.getParticleTexture()
        return (if (rl == null) null else Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(rl.toString()))
                ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
    }

    private val cache = CacheBuilder.newBuilder().expireAfterAccess(42, TimeUnit.SECONDS).build<String, List<IBakery>>()

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): MutableList<BakedQuad> {
        Minecraft.getMinecraft().profiler.startSection("SelfRenderingBakedModel")
        try {
            val layer = MinecraftForgeClient.getRenderLayer()
            val stack = this.itemStack
            val key = layer?.toString() ?: "NO LAYER"

            return this.cache.get(key, {
                // TeslaThingiesMod.logger.info("Getting bakeries for '$key'.")
                this.renderer.getBakeries(layer, state, stack, side, rand, this.transform ?: TRSRTransformation.identity())
            }).fold(mutableListOf()) { list, bakery -> list.also { it.addAll(bakery.getQuads(state, stack, side, this.format, this.transform ?: TRSRTransformation.identity())) } }
        } finally {
            Minecraft.getMinecraft().profiler.endSection()
        }
    }

    protected open val itemStack: ItemStack?
        get() = null

    override fun isBuiltInRenderer() = false
    override fun isAmbientOcclusion() = false
    override fun isGui3d() = false
    override fun getOverrides() = ItemOverrideList.NONE!!

    private val transforms: MutableMap<ItemCameraTransforms.TransformType, TRSRTransformation?> = EnumMap(ItemCameraTransforms.TransformType::class.java)
    private val flipX = TRSRTransformation(null, null, Vector3f(-1f, 1f, 1f), null)

    override fun handlePerspective(type: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> {
        return Pair.of<IBakedModel, Matrix4f>(this, (transforms[type] ?: this.getTransform(0f, 0f, 0f, 0f, 0f, 0f, 1f)).matrix)
    }

    protected fun setTransform(type: ItemCameraTransforms.TransformType, transform: TRSRTransformation) {
        transforms[type] = transform
    }

    protected fun addThirdPersonTransform(transform: TRSRTransformation) {
        setTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, transform)
        setTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, this.toLeftHand(transform))
    }

    protected fun addFirstPersonTransform(transform: TRSRTransformation) {
        setTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, transform)
        setTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, this.toLeftHand(transform))
    }

    protected fun toLeftHand(transform: TRSRTransformation): TRSRTransformation {
        return TRSRTransformation.blockCenterToCorner(this.flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(this.flipX))
    }

    protected fun getTransform(tx: Float, ty: Float, tz: Float, ax: Float, ay: Float, az: Float, s: Float): TRSRTransformation {
        return TRSRTransformation.blockCenterToCorner(TRSRTransformation(
                Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(Vector3f(ax, ay, az)),
                Vector3f(s, s, s),
                null))
    }
}