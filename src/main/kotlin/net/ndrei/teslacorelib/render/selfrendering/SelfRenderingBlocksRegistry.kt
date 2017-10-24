package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler
import java.util.function.Function

@Suppress("unused")
object SelfRenderingBlocksRegistry {
    fun initialize(asm: ASMDataTable, container: ModContainer?) {
        if (TeslaCoreLib.isClientSide) {
            SelfRenderingModelLoader.initialize(asm, container)
        }
    }

    fun addBlock(block: ISelfRenderingBlock) {
        if (TeslaCoreLib.isClientSide) {
            SelfRenderingModelLoader.addBlock(block)
        }
    }

    @SideOnly(Side.CLIENT)
    object SelfRenderingModelLoader: ICustomModelLoader {
        private val blocks = mutableListOf<ISelfRenderingBlock>()
        private val models = mutableMapOf<String, IModel>()

        init {
            MinecraftForge.EVENT_BUS.register(this)
            ModelLoaderRegistry.registerLoader(this)
        }

        fun initialize(asm: ASMDataTable, container: ModContainer?) {
            object: BaseAnnotationHandler<ISelfRenderingBlock>({ it, _, _ ->
                this.blocks.add(it)
            }, SelfRenderingBlock::class) {}.process(asm, container)
        }

        fun addBlock(block: ISelfRenderingBlock) {
            this.blocks.add(block)
        }

        @SubscribeEvent
        fun stitchEvent(ev: TextureStitchEvent) {
            val stuff = mutableListOf<String>()
            if (ev.map == Minecraft.getMinecraft().textureMapBlocks) {
                // ev.map.registerSprite(Textures.MULTI_TANK_SIDE.resource)
                blocks.forEach {
                    it.getTextures().forEach {
                        if (!stuff.contains(it.toString())) {
                            // try to avoid double registering same resource
                            ev.map.registerSprite(it)
                            stuff.add(it.toString())
                        }
                    }
                }
            }
            // TODO: rebake all the thing!... maybe?... I don't know...
        }

        override fun loadModel(modelLocation: ResourceLocation?): IModel {
            // TODO: maybe throw an error if the location is not accepted?
            val block = blocks.first { it.getRegistryName() == modelLocation }
            return models.getOrPut(modelLocation.toString()) {
                if (modelLocation is ModelResourceLocation) {
                    return when (modelLocation.variant) {
                        "inventory" -> SelfRenderingInventoryModel(block)
                        else -> {
                            if (block is IProvideVariantTransform) {
                                return SelfRenderingModel(block, block.getTransform(modelLocation.variant))
                            }
                            return when {
                                modelLocation.variant.contains("facing=north") -> SelfRenderingModel(block, getTransform(0, 0)) // EnumFacing.NORTH))
                                modelLocation.variant.contains("facing=south") -> SelfRenderingModel(block, getTransform(0, 180)) // EnumFacing.SOUTH))
                                modelLocation.variant.contains("facing=east") -> SelfRenderingModel(block, getTransform(0, 90)) // EnumFacing.EAST))
                                modelLocation.variant.contains("facing=west") -> SelfRenderingModel(block, getTransform(0, 270)) // EnumFacing.WEST))
                                else -> SelfRenderingModel(block, TRSRTransformation.identity())
                            }
                        }
                    }
                }
                else return SelfRenderingInventoryModel(block)
            }
        }

        fun getTransform(/*facing: EnumFacing*/ xRot: Int, yRot: Int): TRSRTransformation {
            return TRSRTransformation(ModelRotation.getModelRotation(xRot, yRot))
        }

        override fun accepts(modelLocation: ResourceLocation?): Boolean {
            val rl = modelLocation ?: return false
            return blocks.any { it.getRegistryName() == rl }
        }

        override fun onResourceManagerReload(resourceManager: IResourceManager?) {
            // TODO: rebake all the thing!
        }
    }

    @SideOnly(Side.CLIENT)
    class SelfRenderingModel(val block: ISelfRenderingBlock, val transform: TRSRTransformation): IModel {
        override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
            return SelfRenderingBakedModel(this.block, format, transform)
        }
    }

    @SideOnly(Side.CLIENT)
    class SelfRenderingInventoryModel(val block: ISelfRenderingBlock): IModel {
        override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
            return SelfRenderingInventoryBakedModel(this.block, format)
        }
    }
}
