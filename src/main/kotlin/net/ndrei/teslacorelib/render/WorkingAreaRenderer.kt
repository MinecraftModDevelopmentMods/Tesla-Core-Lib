package net.ndrei.teslacorelib.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

/**
 * Created by CF on 2017-07-12.
 */
@SideOnly(Side.CLIENT)
object WorkingAreaRenderer : TileEntitySpecialRenderer<TileEntity>() {
    private var cR: Float = .9f
    private var cG: Float = .7f
    private var cB: Float = .0f

    override fun render(te: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        val provider = (te as? IWorkAreaProvider) ?: return

        val area = provider.getWorkArea()
        val color = provider.getWorkAreaColor()

        this.cR = (color shr 16 and 255).toFloat() / 255.0f
        this.cB = (color shr 8 and 255).toFloat() / 255.0f
        this.cG = (color and 255).toFloat() / 255.0f

        GlStateManager.pushMatrix()
        GlStateManager.pushAttrib()

        GlStateManager.translate(x.toFloat(), y.toFloat(), z.toFloat())
        RenderHelper.disableStandardItemLighting()
        Minecraft.getMinecraft().entityRenderer.disableLightmap()
        GlStateManager.disableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableAlpha()
        GlStateManager.glLineWidth(2f)
        GlStateManager.color(1f, 1f, 1f)

        val box = area.boundingBox.offset(-te.pos.x.toDouble(), -te.pos.y.toDouble(), -te.pos.z.toDouble())
        this.renderCubeOutline(box)

        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

        this.renderCubeFaces(box)

        GlStateManager.glLineWidth(1f)
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableLighting()
        RenderHelper.enableStandardItemLighting()

        Minecraft.getMinecraft().entityRenderer.enableLightmap()
        GlStateManager.enableTexture2D()
        GlStateManager.popAttrib()
        GlStateManager.popMatrix()
    }

    private fun BufferBuilder.posEx(x: Double, y: Double, z: Double): BufferBuilder {
        this.pos(x, y, z).color(this@WorkingAreaRenderer.cR, this@WorkingAreaRenderer.cG, this@WorkingAreaRenderer.cB, 1f).endVertex()
        return this
    }

    private fun BufferBuilder.posExA(x: Double, y: Double, z: Double): BufferBuilder {
        this.pos(x, y, z).color(this@WorkingAreaRenderer.cR, this@WorkingAreaRenderer.cG, this@WorkingAreaRenderer.cB, .42f).endVertex()
        return this
    }

    private fun renderCubeOutline(pos: AxisAlignedBB) {
        val buffer = Tessellator.getInstance().buffer
        
        val x1 = pos.minX
        val x2 = pos.maxX
        val y1 = pos.minY
        val y2 = pos.maxY
        val z1 = pos.minZ
        val z2 = pos.maxZ
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

        buffer.posEx(x1, y1, z1)
        buffer.posEx(x2, y1, z1)
        buffer.posEx(x1, y1, z1)
        buffer.posEx(x1, y2, z1)
        buffer.posEx(x1, y1, z1)
        buffer.posEx(x1, y1, z2)
        buffer.posEx(x2, y2, z2)
        buffer.posEx(x1, y2, z2)
        buffer.posEx(x2, y2, z2)
        buffer.posEx(x2, y1, z2)
        buffer.posEx(x2, y2, z2)
        buffer.posEx(x2, y2, z1)
        buffer.posEx(x1, y2, z1)
        buffer.posEx(x1, y2, z2)
        buffer.posEx(x1, y2, z1)
        buffer.posEx(x2, y2, z1)
        buffer.posEx(x2, y1, z1)
        buffer.posEx(x2, y1, z2)
        buffer.posEx(x2, y1, z1)
        buffer.posEx(x2, y2, z1)
        buffer.posEx(x1, y1, z2)
        buffer.posEx(x2, y1, z2)
        buffer.posEx(x1, y1, z2)
        buffer.posEx(x1, y2, z2)

        Tessellator.getInstance().draw()
    }

    private fun renderCubeFaces(pos: AxisAlignedBB) {
        val buffer = Tessellator.getInstance().buffer

        val x1 = pos.minX
        val x2 = pos.maxX
        val y1 = pos.minY
        val y2 = pos.maxY
        val z1 = pos.minZ
        val z2 = pos.maxZ
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        buffer.posExA(x1, y1, z1).posExA(x1, y2, z1).posExA(x2, y2, z1).posExA(x2, y1, z1)
        buffer.posExA(x1, y1, z2).posExA(x2, y1, z2).posExA(x2, y2, z2).posExA(x1, y2, z2)

        buffer.posExA(x1, y1, z1).posExA(x2, y1, z1).posExA(x2, y1, z2).posExA(x1, y1, z2)
        buffer.posExA(x1, y2, z1).posExA(x1, y2, z2).posExA(x2, y2, z2).posExA(x2, y2, z1)

        buffer.posExA(x1, y1, z1).posExA(x1, y1, z2).posExA(x1, y2, z2).posExA(x1, y2, z1)
        buffer.posExA(x2, y1, z1).posExA(x2, y2, z1).posExA(x2, y2, z2).posExA(x2, y1, z2)

        Tessellator.getInstance().draw()
    }
}