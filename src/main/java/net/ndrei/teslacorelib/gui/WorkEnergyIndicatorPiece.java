package net.ndrei.teslacorelib.gui;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.ndrei.teslacorelib.tileentities.IWorkEnergyProvider;

import java.util.List;

/**
 * Created by CF on 2016-12-27.
 */
public class WorkEnergyIndicatorPiece extends BasicContainerGuiPiece {
    private IWorkEnergyProvider provider;

    public WorkEnergyIndicatorPiece(IWorkEnergyProvider provider, int left, int top) {
        super(left, top, 36, 4);

        this.provider = provider;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        container.bindDefaultTexture();

        container.drawTexturedModalRect(guiX + this.getLeft(), guiY + this.getTop(), 1, 245, this.getWidth(), this.getHeight());
        if (this.provider != null) {
            float percent = (float)this.provider.getWorkEnergyStored() / (float)this.provider.getWorkEnergyCapacity();
            int width = Math.max(0, Math.min(this.getWidth() - 2, Math.round((this.getWidth() - 2) * percent)));
            if (width > 0) {
                container.drawTexturedModalRect(
                        guiX + this.getLeft() + 1, guiY + this.getTop() + 1,
                        2, 251, width, this.getHeight() - 2);
            }
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        if (super.isInside(container, mouseX, mouseY) && (this.provider != null)) {
            List<String> lines = Lists.newArrayList();
            lines.add(String.format("%sWork Energy Buffer", ChatFormatting.DARK_PURPLE));
            lines.add(String.format("%s%,d T %sof %s%,d T",
                    ChatFormatting.AQUA, this.provider.getWorkEnergyStored(),
                    ChatFormatting.DARK_GRAY,
                    ChatFormatting.RESET, this.provider.getWorkEnergyCapacity()));
            lines.add(String.format("%smax: %s+%,d T %s/ tick",
                    ChatFormatting.GRAY,
                    ChatFormatting.AQUA, this.provider.getWorkEnergyTick(),
                    ChatFormatting.GRAY));

            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
        }
    }
}
