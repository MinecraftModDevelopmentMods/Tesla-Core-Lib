package net.ndrei.teslacorelib.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;

/**
 * Created by CF on 2017-01-15.
 */
public abstract class ToggleButtonPiece extends BasicContainerGuiPiece {
    public ToggleButtonPiece(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    protected abstract int getCurrentState();
    protected abstract void renderState(BasicTeslaGuiContainer container, int state, BoundingRectangle box);
    protected abstract void clicked();

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        mouseX -= container.getGuiLeft();
        mouseY -= container.getGuiTop();
        if ((mouseX >= this.getLeft()) && (mouseY >= this.getTop()) && (mouseX <= this.getLeft() + this.getWidth()) && (mouseY <= this.getTop() + this.getHeight())) {
            container.drawFilledRect(container.getGuiLeft() + this.getLeft(), container.getGuiTop() + this.getTop(), this.getWidth(), this.getHeight(), 0x42FFFFFF);
        }
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        int state = this.getCurrentState();
        int x = this.getLeft() + (this.getWidth() - 16) / 2;
        int y = this.getTop() + (this.getHeight() - 16) / 2;
        this.renderState(container, state, new BoundingRectangle(x, y, 16, 16));
    }

    protected void renderItemStack(BasicTeslaGuiContainer container, ItemStack stack, BoundingRectangle box) {
        if (stack != null) {
            RenderItem item = container.getItemRenderer();

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            container.getItemRenderer().renderItemAndEffectIntoGUI(stack, box.getLeft(), box.getTop());
            container.getItemRenderer().renderItemOverlayIntoGUI(container.mc.fontRendererObj, stack, box.getLeft(), box.getTop(), null);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            item.renderItemOverlayIntoGUI(container.getFontRenderer(), stack,
                    box.getLeft(), box.getTop(), null);
        }
    }

    @Override
    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
        if (BasicContainerGuiPiece.isInside(container, this, mouseX, mouseY)) {
            this.clicked();
        }
    }
}
