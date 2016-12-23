package net.ndrei.teslacorelib.gui;

import com.google.common.collect.Lists;
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo;

import java.util.List;

/**
 * Created by CF on 2016-12-22.
 */
public class SideConfigSelector extends BasicContainerGuiPiece {
    private SidedItemHandlerConfig sidedConfig;
    private SideConfigurator configurator;
    private int selectedInventory = -1;

    public SideConfigSelector(int left, int top, int width, int height, SidedItemHandlerConfig sidedConfig, SideConfigurator configurator) {
        super(left, top, width, height);

        this.sidedConfig = sidedConfig;
        this.configurator = configurator;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();
        if ((colors != null) && (colors.size() > 0)) {
            container.bindDefaultTexture();
            for (int i = 0; (i < colors.size()) && (i < 9); i++) {
                container.drawTexturedRect(
                        this.getLeft() + 2 + i * 18, this.getTop() + 2,
                        (i == this.selectedInventory) ? 128 : 110, 210, 14, 14);
                container.drawFilledRect(
                        guiX + this.getLeft() + 4 + i * 18, guiY + this.getTop() + 4,
                        10, 10, 0xFF000000 + colors.get(i).getColor().getMapColor().colorValue);
            }
        }
    }

    @Override
    public void drawForegroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();

        if (this.isInside(container, mouseX, mouseY)) {
            int localY = mouseY - guiY - this.getTop();
            if ((localY >= 2) && (localY <= 14)) {
                int localX = mouseX - guiX - this.getLeft();
                int index = localX / 18;
                if ((index != this.selectedInventory) && (index >= 0) && (index < colors.size()) && (colors.get(index).getHighlight() != null)) {
                    localX = localX - index * 18;
                    if ((localX >= 2) && (localX <= 14)) {
                        BoundingRectangle box = colors.get(index).getHighlight();
                        container.drawFilledRect(box.getLeft(), box.getTop(), box.getWidth(), box.getHeight(),
                                0x42000000 + colors.get(index).getColor().getMapColor().colorValue,
                                0xFF000000 + colors.get(index).getColor().getMapColor().colorValue);
                    }
                }
            }
        }

        if ((this.selectedInventory >= 0) && (this.selectedInventory < colors.size())) {
            BoundingRectangle box = colors.get(this.selectedInventory).getHighlight();
            container.drawFilledRect(box.getLeft(), box.getTop(), box.getWidth(), box.getHeight(),
                    0x42000000 + colors.get(this.selectedInventory).getColor().getMapColor().colorValue,
                    0xFF000000 + colors.get(this.selectedInventory).getColor().getMapColor().colorValue);
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        if (this.isInside(container, mouseX, mouseY)) {
            int localY = mouseY - guiY - this.getTop();
            if ((localY >= 2) && (localY <= 14)) {
                int localX = mouseX - guiX - this.getLeft();
                int index = localX / 18;
                List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();
                if ((index >= 0) && (index < colors.size()) && (colors.get(index).getHighlight() != null)) {
                    localX = localX - index * 18;
                    if ((localX >= 2) && (localX <= 14)) {
                        String label = colors.get(index).getName();
                        if ((label != null) && (label.length() > 0)) {
                            container.drawTooltip(Lists.newArrayList(label),
                                    this.getLeft() + (index * 18) + 9,
                                    this.getTop() + this.getHeight() / 2);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(BasicTeslaGuiContainer container, int mouseX, int mouseY, int mouseButton) {
        int oldIndex = this.selectedInventory;
        this.selectedInventory = -1;
        if (this.isInside(container, mouseX, mouseY)) {
            int localY = mouseY - container.getGuiTop() - this.getTop();
            if ((localY >= 2) && (localY <= 14)) {
                int localX = mouseX - container.getGuiLeft() - this.getLeft();
                int index = localX / 18;
                List<ColoredItemHandlerInfo> colors = this.sidedConfig.getColoredInfo();
                if ((index != oldIndex) && (index >= 0) && (index < colors.size()) && (colors.get(index).getHighlight() != null)) {
                    localX = localX - index * 18;
                    if ((localX >= 2) && (localX <= 14)) {
                        this.selectedInventory = index;
                    }
                }
            }
        }

        if (this.configurator != null) {
            this.configurator.setSelectedInventory(this.selectedInventory);
        }

        BasicTeslaContainer slots = container.getTeslaContainer();
        if (slots != null) {
            if (this.selectedInventory >= 0) {
                slots.hidePlayerInventory();
            }
            else {
                slots.showPlayerInventory();
            }
        }
    }
}
