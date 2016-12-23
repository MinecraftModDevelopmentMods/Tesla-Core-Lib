package net.ndrei.teslacorelib.gui;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.darkhax.tesla.lib.PowerBar;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.ndrei.teslacorelib.inventory.EnergyStorage;

import java.util.List;

/**
 * Created by CF on 2016-12-22.
 */
public class TeslaEnergyLevelPiece extends BasicContainerGuiPiece {
    private EnergyStorage energyStorage;

    public TeslaEnergyLevelPiece(int left, int top, EnergyStorage energyStorage) {
        super(left, top, 18, 54);
        this.energyStorage = energyStorage;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        BasicTeslaGuiContainer.bindDefaultTexture(container);

        container.drawTexturedRect(this.getLeft(), this.getTop(),
                1, 189, this.getWidth(), this.getHeight());

        if (Loader.isModLoaded("tesla")) {
            this.renderTeslaBackground(container, guiX, guiY);
        }
        else if (this.energyStorage != null) {
            int power = (this.energyStorage.getEnergyStored() * (this.getHeight() - 6)) / this.energyStorage.getMaxEnergyStored();

            container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 20, 191, this.getWidth() - 4, this.getHeight() - 4);
            container.drawTexturedRect(this.getLeft() + 3, this.getTop() + 3 + this.getHeight() - 6 - power, 35, 192 + this.getHeight() - 6 - power, this.getWidth() - 6, power + 2);
        }
    }

    @Optional.Method(modid = "tesla")
    private void renderTeslaBackground(BasicTeslaGuiContainer container, int guiX, int guiY) {
        if (this.energyStorage != null) {
            PowerBar bar = new PowerBar(container,
                    guiX + super.getLeft() + 2,  guiY + this.getTop() + 2,
                    PowerBar.BackgroundType.LIGHT);
            bar.draw(this.energyStorage);
        }
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        if (super.isInside(container, mouseX, mouseY) && (this.energyStorage != null)) {
            List<String> lines = Lists.newArrayList();
            lines.add(String.format("%s%,d T %sof", ChatFormatting.AQUA, this.energyStorage.getEnergyStored(), ChatFormatting.DARK_GRAY));
            lines.add(String.format("%s%,d T", ChatFormatting.RESET, this.energyStorage.getMaxEnergyStored()));
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
        }
    }
}
