package net.ndrei.teslacorelib.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

/**
 * Created by CF on 2016-12-30.
 */
public class MachineNameGuiPiece extends BasicContainerGuiPiece {
    private String unlocalizedName;

    public MachineNameGuiPiece(String unlocalizedName, int left, int top, int width, int height) {
        super(left, top, width, height);

        this.unlocalizedName = unlocalizedName;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        if ((this.unlocalizedName != null) && (this.unlocalizedName.length() > 0)) {
            String title = I18n.format(this.unlocalizedName);
            container.mc.fontRendererObj.drawString(title, guiX + this.getLeft(), guiY + this.getTop(), 4210751);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}
