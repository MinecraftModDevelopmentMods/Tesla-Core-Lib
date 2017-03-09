package net.ndrei.teslacorelib.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.compatibility.FontRendererUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by CF on 2016-12-18.
 */
public class BasicTeslaGuiContainer<T extends SidedTileEntity> extends GuiContainer {
    public static final ResourceLocation MACHINE_BACKGROUND = new ResourceLocation(TeslaCoreLib.MODID, "textures/gui/basic-machine.png");

    private T entity;
    private List<IGuiContainerPiece> pieces;

    private int guiId;

    public BasicTeslaGuiContainer(int id, Container container, T entity) {
        super(container);
        this.guiId = id;
        this.entity = entity;

        super.xSize = 198;
        super.ySize = 184;

//        List<IGuiContainerPiece> pieces = this.entity.getGuiContainerPieces(this);
//        this.pieces = (pieces != null) ? pieces : Lists.newArrayList();
        this.refreshParts();
    }

    public BasicTeslaContainer getTeslaContainer() {
        if (this.inventorySlots instanceof BasicTeslaContainer) {
            return (BasicTeslaContainer)this.inventorySlots;
        }
        return null;
    }

    public int getGuiId() {
        return this.guiId;
    }

    public T getEntity() {
        return this.entity;
    }

    protected void bindDefaultTexture() {
        BasicTeslaGuiContainer.bindDefaultTexture(this);
    }

    public static void bindDefaultTexture(GuiContainer container) {
        container.mc.getTextureManager().bindTexture(MACHINE_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.bindDefaultTexture();
        this.drawTexturedModalRect(super.guiLeft, super.guiTop, 0, 0, super.getXSize(), super.getYSize());

        for(IGuiContainerPiece piece : this.pieces) {
            if (!piece.isVisible()) {
                continue;
            }

            piece.drawBackgroundLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY);
        }

        for(IGuiContainerPiece piece : this.pieces) {
            if (!piece.isVisible()) {
                continue;
            }

            piece.drawMiddleLayer(this, super.guiLeft, super.guiTop, partialTicks, mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for(IGuiContainerPiece piece : this.pieces) {
            if (!piece.isVisible()) {
                continue;
            }

            piece.drawForegroundLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY);
        }

        for(IGuiContainerPiece piece : this.pieces) {
            if (!piece.isVisible()) {
                continue;
            }

            piece.drawForegroundTopLayer(this, super.guiLeft, super.guiTop, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for(IGuiContainerPiece piece : this.pieces) {
            if (piece.isVisible() && BasicContainerGuiPiece.isInside(this, piece, mouseX, mouseY)) {
                piece.mouseClicked(this, mouseX, mouseY, mouseButton);
            }
        }
    }

    public void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height) {
        super.drawTexturedModalRect(super.guiLeft + x, super.guiTop + y, textureX, textureY, width, height);
    }

    public void drawTooltip(List<String> textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y);
    }

    public void drawFilledRect(int x, int y, int width, int height, int color) {
        super.drawGradientRect(x, y, x + width, y + height, color, color);
    }

    public void drawFilledRect(int x, int y, int width, int height, int color, int strokeColor) {
        this.drawFilledRect(x, y, width, height, color);

        super.drawHorizontalLine(x, x + width - 1, y, strokeColor);
        super.drawVerticalLine(x, y, y + height - 1, strokeColor);
        super.drawVerticalLine(x + width - 1, y, y + height - 1, strokeColor);
        super.drawHorizontalLine(x, x + width - 1, y + height - 1, strokeColor);
    }

    public RenderItem getItemRenderer() {
        return super.itemRender;
    }

    public FontRenderer getFontRenderer() {
        return FontRendererUtil.getFontRenderer(); // TODO: not sure if this is different than super.fontRenderer... find out!
    }

    public void setZIndex(float zLevel) {
        this.zLevel = zLevel;
    }

    private void refreshParts() {
        List<IGuiContainerPiece> pieces = this.entity.getGuiContainerPieces(this);
        this.pieces = (pieces != null) ? pieces : Lists.newArrayList();
    }

    public static void refreshParts(World world) {
        if ((world != null) && world.isRemote && (Minecraft.getMinecraft().currentScreen instanceof BasicTeslaGuiContainer)) {
            ((BasicTeslaGuiContainer)Minecraft.getMinecraft().currentScreen).refreshParts();
        }
    }
}
