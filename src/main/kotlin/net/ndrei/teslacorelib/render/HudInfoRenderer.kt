package net.ndrei.teslacorelib.render

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine
import net.ndrei.teslacorelib.compatibility.FontRendererUtil
import net.ndrei.teslacorelib.getFacingFromEntity
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by CF on 2017-06-28.
 */
open class HudInfoRenderer<T : TileEntity> : TileEntitySpecialRenderer<T>() {
    override final fun render(te: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        if (te.hasCapability(TeslaCoreCapabilities.CAPABILITY_HUD_INFO, null)) {
            val info = te.getCapability(TeslaCoreCapabilities.CAPABILITY_HUD_INFO, null)
            val lines = info!!.hudLines

            if (lines != null && lines.size > 0 && this.shouldRender(te)) {
                var side = this.rendererDispatcher.cameraHitResult.sideHit
                if (side == EnumFacing.DOWN || side == EnumFacing.UP) {
                    side = getFacingFromEntity(te.pos, this.rendererDispatcher.entityX, this.rendererDispatcher.entityZ)
                }

                GlStateManager.pushMatrix()

                GlStateManager.translate(x.toFloat() + 0.5f, y.toFloat() + 1.0f, z.toFloat() + 0.5f)
                when (side) {
                    EnumFacing.NORTH -> GlStateManager.rotate(180f, 0.0f, 1.0f, 0.0f)
                    EnumFacing.WEST -> GlStateManager.rotate(-90f, 0.0f, 1.0f, 0.0f)
                    EnumFacing.EAST -> GlStateManager.rotate(90f, 0.0f, 1.0f, 0.0f)
                    /*EnumFacing.SOUTH*/ else -> {
                    }
                }
                GlStateManager.translate(0.0, 0.0, 0.5)

                super.setLightmapDisabled(true)
                renderText(lines, 1f)
                super.setLightmapDisabled(false)
                GlStateManager.popMatrix()
            }
        }
    }

    protected open fun shouldRender(te: T): Boolean {
        return this.rendererDispatcher.cameraHitResult != null && te.pos == this.rendererDispatcher.cameraHitResult.blockPos
    }

    private fun renderText(messages: List<HudInfoLine>, scale: Float) {
        val font = FontRendererUtil.fontRenderer
        GlStateManager.pushMatrix()

        GlStateManager.translate(-0.5f, 0f, 0.01f)
        val magicNumber = 0.0075f
        GlStateManager.scale(magicNumber * scale, -magicNumber * scale, magicNumber)
        GlStateManager.glNormal3f(0.0f, 0.0f, 1.0f)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

        val blockSize = Math.round(scale * .9f / magicNumber)
        val padding = Math.round(scale * .05f / magicNumber)

        val height = 11
        val logSize = messages.size
        var y = -height * logSize - height / 2
        for (ctl in messages) {
            if (ctl.background != null) {
                drawRectangle(ctl.background, true, padding.toDouble(), y.toDouble(), blockSize.toDouble(), height.toDouble(), -0.03)
            }

            if ((ctl.percent > 0) && (ctl.percentColor != null)) {
                val percent = Math.max(0.0, Math.min(1.0, ctl.percent.toDouble()))
                drawRectangle(ctl.percentColor!!, true, padding.toDouble(), y.toDouble(), blockSize * percent, height.toDouble(), -0.02)
            }

            if (ctl.border != null) {
                drawRectangle(ctl.border, false, padding.toDouble(), y.toDouble(), blockSize.toDouble(), height.toDouble(), -0.01)
            }

            val line = font.trimStringToWidth(ctl.text, blockSize - 2)
            if (ctl.alignment == HudInfoLine.TextAlignment.LEFT) {
                font.drawString(line,
                        padding + 1,
                        y + 2, if (ctl.color == null) 16777215 else ctl.color.rgb)
            } else {
                val textWidth = Math.min(font.getStringWidth(ctl.text), blockSize - 2)
                if (ctl.alignment == HudInfoLine.TextAlignment.RIGHT) {
                    font.drawString(line,
                            padding + 1 + blockSize - 2 - textWidth,
                            y + 2, if (ctl.color == null) 16777215 else ctl.color.rgb)
                } else if (ctl.alignment == HudInfoLine.TextAlignment.CENTER) {
                    font.drawString(line,
                            padding + 1 + (blockSize - 2 - textWidth) / 2,
                            y + 2, if (ctl.color == null) 16777215 else ctl.color.rgb)
                }
            }
            y += height
        }

        GlStateManager.popMatrix()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    private fun drawRectangle(color: Color, filled: Boolean, x: Double, y: Double, width: Double, height: Double, zTranslate: Double) {
        val red = color.red / 255.0f
        val green = color.green / 255.0f
        val blue = color.blue / 255.0f
        val alpha = color.alpha / 255.0f

        GlStateManager.pushAttrib()
        GlStateManager.pushMatrix()
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer

        if (!filled) {
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)

            buffer.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex()
        } else {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

            buffer.pos(x, y + 0, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex()
            buffer.pos(x + width, y + 0, 0.0).color(red, green, blue, alpha).endVertex()
        }

        GlStateManager.translate(0.0, 0.0, zTranslate)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
        GlStateManager.disableLighting()
        GlStateManager.disableTexture2D()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
        GlStateManager.popAttrib()
    }
}
