package net.ndrei.teslacorelib.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.ndrei.teslacorelib.Utils;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.capabilities.hud.IHudInfoProvider;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings("unused")
public class HudInfoRenderer<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    @Override
    public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.hasCapability(TeslaCoreCapabilities.CAPABILITY_HUD_INFO, null)) {
            IHudInfoProvider info = te.getCapability(TeslaCoreCapabilities.CAPABILITY_HUD_INFO, null);
            List<HudInfoLine> lines = info.getHUDLines();

            if ((lines != null) && (lines.size() > 0) && this.shouldRender(te)) {
                EnumFacing side = this.rendererDispatcher.cameraHitResult.sideHit;
                if ((side == EnumFacing.DOWN) || (side == EnumFacing.UP)) {
                    side = Utils.getFacingFromEntity(te.getPos(), this.rendererDispatcher.entityX, this.rendererDispatcher.entityZ);
                }

                GlStateManager.pushMatrix();

                GlStateManager.translate((float) x + 0.5F, (float) y + 1.0F, (float) z + 0.5F);
                switch(side) {
                    case NORTH:
                        GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
                        break;
                    case WEST:
                        GlStateManager.rotate(-90, 0.0F, 1.0F, 0.0F);
                        break;
                    case EAST:
                        GlStateManager.rotate(90, 0.0F, 1.0F, 0.0F);
                        break;
                    case SOUTH:
                        // GlStateManager.rotate(0, 0.0F, 1.0F, 0.0F);
                        break;
                }
                GlStateManager.translate(0.0D, 0.0D, 0.5D);

                super.setLightmapDisabled(true);
                renderText(lines, 1);
                super.setLightmapDisabled(false);
                GlStateManager.popMatrix();
            }
        }
    }

    protected  boolean shouldRender(T te) {
         return ((this.rendererDispatcher.cameraHitResult != null)
                 && (te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos()))
         );
    }

    private void renderText(List<HudInfoLine> messages, float scale)
    {
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        GlStateManager.pushMatrix();

        GlStateManager.translate(-0.5F, 0F, 0.01F);
        float magicNumber = 0.0075F;
        GlStateManager.scale(magicNumber * scale, -magicNumber * scale, magicNumber);
        GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int blockSize = Math.round((scale * .9f) / magicNumber);
        int padding = Math.round((scale * .05f) / magicNumber);

        int height = 11;
        int logSize = messages.size();
        int y = - height * logSize - height / 2;
        for (HudInfoLine ctl : messages)
        {
            if (ctl.background != null) {
                drawRectangle(ctl.background, true, padding, y, blockSize, height, -0.03);
            }

            if ((ctl.percent > 0) && (ctl.percentColor != null)) {
                double percent = Math.max(0.0D, Math.min(1.0D, ctl.percent));
                drawRectangle(ctl.percentColor, true, padding, y, blockSize * percent, height, -0.02);
            }

            if (ctl.border != null) {
                drawRectangle(ctl.border, false, padding, y, blockSize, height, - 0.01);
            }

            String line = font.trimStringToWidth(ctl.text, blockSize - 2);
            if (ctl.alignment == HudInfoLine.TextAlignment.LEFT) {
                font.drawString(line,
                        padding + 1,
                        y + 2, (ctl.color == null) ? 16777215 : ctl.color.getRGB());
            }
            else {
                int textWidth = Math.min(font.getStringWidth(ctl.text), blockSize - 2);
                if (ctl.alignment == HudInfoLine.TextAlignment.RIGHT) {
                    font.drawString(line,
                            padding + 1 + blockSize - 2 - textWidth,
                            y + 2, (ctl.color == null) ? 16777215 : ctl.color.getRGB());
                } else if (ctl.alignment == HudInfoLine.TextAlignment.CENTER) {
                    font.drawString(line,
                            padding + 1 + (blockSize - 2 - textWidth) / 2,
                            y + 2, (ctl.color == null) ? 16777215 : ctl.color.getRGB());
                }
            }
            y += height;
        }

        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawRectangle(Color color, boolean filled, double x, double y, double width, double height, double zTranslate) {
        float red = color.getRed() / 255.0f;
        float green = color.getGreen() / 255.0f;
        float blue = color.getBlue() / 255.0f;
        float alpha = color.getAlpha() / 255.0f;

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        if (!filled) {
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

            buffer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
        }
        else {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            buffer.pos(x, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
            buffer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
        }

        GlStateManager.translate(0F, 0F, zTranslate);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}
