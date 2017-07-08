package net.ndrei.teslacorelib.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.crash.CrashReport
import net.minecraft.item.ItemStack
import net.minecraft.util.ReportedException
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.compatibility.findDeclaredField
import net.ndrei.teslacorelib.compatibility.findDeclaredMethod

/**
 * Created by CF on 2017-07-05.
 */
@SideOnly(Side.CLIENT)
class GhostedItemRenderer(private val render: RenderItem) {
    private var currentAlpha: Float = .42f

    fun renderItemInGUI(stack: ItemStack, x: Int, y: Int, alpha: Float = .42f) {
        this.currentAlpha = alpha
        if (!stack.isEmpty) {
            val player = Minecraft.getMinecraft().player
            this.render.zLevel += 50.0f

            try {
                this.renderItemModelIntoGUI(stack, x, y, this.render.getItemModelWithOverrides(stack, null, player))
            } catch (throwable: Throwable) {
                val crashReport = CrashReport.makeCrashReport(throwable, "Rendering item")
                val crashReportCategory = crashReport.makeCategory("Item being rendered")
                crashReportCategory.addDetail("Item Type") { (stack.getItem() as Any).toString() }
                crashReportCategory.addDetail("Item Aux") { stack.getMetadata().toString() }
                crashReportCategory.addDetail("Item NBT") { (stack.getTagCompound() as Any).toString() }
                crashReportCategory.addDetail("Item Foil") { stack.hasEffect().toString() }
                throw ReportedException(crashReport)
            }

            this.render.zLevel -= 50.0f
        }
    }

    private fun renderItemModelIntoGUI(stack: ItemStack, x: Int, y: Int, bakedmodel: IBakedModel) {
        val tmField = this.render.javaClass.findDeclaredField("textureManager") ?: return
        tmField.isAccessible = true
        val textureManager = tmField.get(this.render) as TextureManager

        val sgtMethod = this.render.javaClass.findDeclaredMethod("setupGuiTransform",
                java.lang.Integer.TYPE, java.lang.Integer.TYPE, java.lang.Boolean.TYPE) ?: return
        sgtMethod.isAccessible = true

        GlStateManager.pushMatrix()
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false)
        GlStateManager.enableRescaleNormal()
//        GlStateManager.enableAlpha()
//        GlStateManager.alphaFunc(GL11.GL_GREATER, .8F)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.color(1.0f, 1.0f, 1.0f, .42f)
        // this.render.setupGuiTransform(x, y, bakedmodel.isGui3d)
        sgtMethod.invoke(this.render, x, y, bakedmodel.isGui3d)
        val itemBakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false)
        // this.render.renderItem(stack, bakedmodel)
        this.renderItem(stack, itemBakedModel)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.disableAlpha()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableLighting()
        GlStateManager.popMatrix()
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap()
    }

    private fun renderItem(stack: ItemStack, model: IBakedModel) {
        val rmMethod = this.render.javaClass.findDeclaredMethod("renderModel",
                IBakedModel::class.java, java.lang.Integer.TYPE, ItemStack::class.java) ?: return
        rmMethod.isAccessible = true

        val reMethod = this.render.javaClass.findDeclaredMethod("renderEffect",
                IBakedModel::class.java) ?: return
        reMethod.isAccessible = true

        if (!stack.isEmpty) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(-0.5f, -0.5f, -0.5f)

            if (model.isBuiltInRenderer) {
                // GlStateManager.color(1.0f, 1.0f, 1.0f, .42f)
                GlStateManager.enableRescaleNormal()
                TileEntityItemStackRenderer.instance.renderByItem(stack)
            } else {
                // this.render.renderModel(model, stack)
                rmMethod.invoke(this.render, model, (this.currentAlpha * 255f).toInt().shl(24) + 0xFFFFFF, stack)

                if (stack.hasEffect()) {
                    // this.render.renderEffect(model)
                    reMethod.invoke(this.render, model)
                }
            }

            GlStateManager.popMatrix()
        }
    }

    companion object {
        fun renderItemInGUI(render: RenderItem, stack: ItemStack, x: Int, y: Int, alpha: Float = .42f) {
            GhostedItemRenderer(render).renderItemInGUI(stack, x, y, alpha)
        }
    }
}