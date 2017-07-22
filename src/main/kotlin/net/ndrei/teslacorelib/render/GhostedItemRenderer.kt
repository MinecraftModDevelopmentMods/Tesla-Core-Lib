package net.ndrei.teslacorelib.render

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.color.ItemColors
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.crash.CrashReport
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ReportedException
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by CF on 2017-07-05.
 */
@SideOnly(Side.CLIENT)
class GhostedItemRenderer(private val render: RenderItem) {
    var zLevel: Float = 0.0f
    private val RES_ITEM_GLINT = ResourceLocation("textures/misc/enchanted_item_glint.png")

    fun renderItemInGUI(stack: ItemStack, x: Int, y: Int, alpha: Float = .42f) {
        if (!stack.isEmpty) {
            val player = Minecraft.getMinecraft().player
            this.zLevel += 50.0f

            try {
                this.renderItemModelIntoGUI(stack, x, y, this.render.getItemModelWithOverrides(stack, null, player), alpha)
            } catch (throwable: Throwable) {
                val crashReport = CrashReport.makeCrashReport(throwable, "Rendering item")
                val crashReportCategory = crashReport.makeCategory("Item being rendered")
                crashReportCategory.addDetail("Item Type") { (stack.getItem() as Any).toString() }
                crashReportCategory.addDetail("Item Aux") { stack.getMetadata().toString() }
                crashReportCategory.addDetail("Item NBT") { (stack.getTagCompound() as Any).toString() }
                crashReportCategory.addDetail("Item Foil") { stack.hasEffect().toString() }
                throw ReportedException(crashReport)
            }

            this.zLevel -= 50.0f
        }
    }

    //#region stolen from RenderItem

    fun getItemModelWithOverrides(stack: ItemStack, worldIn: World?, entitylivingbaseIn: EntityLivingBase?): IBakedModel {
        val baked = this.render.itemModelMesher.getItemModel(stack)
        return baked.overrides.handleItemState(baked, stack, worldIn, entitylivingbaseIn)
    }

    val textureManager: TextureManager
        get() = Minecraft.getMinecraft().renderEngine

    val itemColors: ItemColors
        get() = Minecraft.getMinecraft().itemColors

    private  fun renderItemModelIntoGUI(stack: ItemStack, x: Int, y: Int, bakedmodel: IBakedModel, alpha: Float) {
        GlStateManager.pushMatrix()
        this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
        GlStateManager.enableRescaleNormal()
//        GlStateManager.enableAlpha()
//        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha)
        this.setupGuiTransform(x, y, bakedmodel.isGui3d)
        val finalModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false)
        this.renderItem(stack, finalModel, alpha)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
//        GlStateManager.disableAlpha()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GlStateManager.popMatrix()
        this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
    }

    fun renderItem(stack: ItemStack, model: IBakedModel, alpha: Float) {
        if (!stack.isEmpty) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(-0.5f, -0.5f, -0.5f)

            if (model.isBuiltInRenderer) {
//                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                GlStateManager.enableRescaleNormal()
                TileEntityItemStackRenderer.instance.renderByItem(stack)
            } else {
                this.renderModel(model, stack, alpha)

                if (stack.hasEffect()) {
                    this.renderEffect(model)
                }
            }

            GlStateManager.popMatrix()
        }
    }

    private fun renderEffect(model: IBakedModel) {
        GlStateManager.depthMask(false)
        GlStateManager.depthFunc(514)
        GlStateManager.disableLighting()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE)
        this.textureManager.bindTexture(RES_ITEM_GLINT)
        GlStateManager.matrixMode(5890)
        GlStateManager.pushMatrix()
        GlStateManager.scale(8.0f, 8.0f, 8.0f)
        val f = (Minecraft.getSystemTime() % 3000L).toFloat() / 3000.0f / 8.0f
        GlStateManager.translate(f, 0.0f, 0.0f)
        GlStateManager.rotate(-50.0f, 0.0f, 0.0f, 1.0f)
        this.renderModel(model, -8372020, 1.0f)
        GlStateManager.popMatrix()
        GlStateManager.pushMatrix()
        GlStateManager.scale(8.0f, 8.0f, 8.0f)
        val f1 = (Minecraft.getSystemTime() % 4873L).toFloat() / 4873.0f / 8.0f
        GlStateManager.translate(-f1, 0.0f, 0.0f)
        GlStateManager.rotate(10.0f, 0.0f, 0.0f, 1.0f)
        this.renderModel(model, -8372020, 1.0f)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableLighting()
        GlStateManager.depthFunc(515)
        GlStateManager.depthMask(true)
        this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    }

    private fun renderModel(model: IBakedModel, stack: ItemStack, alpha: Float) {
        this.renderModel(model, (alpha * 255f).toInt().shl(24) + 0xFFFFFF, stack, alpha)
    }

    private fun renderModel(model: IBakedModel, color: Int, alpha: Float) {
        this.renderModel(model, color, ItemStack.EMPTY, alpha)
    }

    private fun renderModel(model: IBakedModel, color: Int, stack: ItemStack, alpha: Float) {
        val tessellator = Tessellator.getInstance()
        val bufferbuilder = tessellator.buffer
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM)

        for (enumfacing in EnumFacing.values()) {
            this.renderQuads(bufferbuilder, model.getQuads(null as IBlockState?, enumfacing, 0L), color, stack, alpha)
        }

        this.renderQuads(bufferbuilder, model.getQuads(null as IBlockState?, null as EnumFacing?, 0L), color, stack, alpha)
        tessellator.draw()
    }

    private fun renderQuads(renderer: BufferBuilder, quads: List<BakedQuad>, color: Int, stack: ItemStack, alpha: Float) {
        val flag = /*color == -1 &&*/ !stack.isEmpty
        var i = 0

        val j = quads.size
        while (i < j) {
            val bakedquad = quads[i]
            var k = color

            if (flag && bakedquad.hasTintIndex()) {
                k = this.itemColors.getColorFromItemstack(stack, bakedquad.tintIndex)

                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k)
                }

                k = (alpha * 255f).toInt().shl(24) + (k or -16777216)
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k)
            ++i
        }
    }

    private fun setupGuiTransform(xPosition: Int, yPosition: Int, isGui3d: Boolean) {
        GlStateManager.translate(xPosition.toFloat(), yPosition.toFloat(), 100.0f + this.zLevel)
        GlStateManager.translate(8.0f, 8.0f, 0.0f)
        GlStateManager.scale(1.0f, -1.0f, 1.0f)
        GlStateManager.scale(16.0f, 16.0f, 16.0f)

        if (isGui3d) {
            GlStateManager.enableLighting()
        } else {
            GlStateManager.disableLighting()
        }
    }

    //#endregion

    companion object {
        fun renderItemInGUI(render: RenderItem, stack: ItemStack, x: Int, y: Int, alpha: Float = .42f) {
            GhostedItemRenderer(render).renderItemInGUI(stack, x, y, alpha)
        }
    }
}