package net.ndrei.teslacorelib.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.localization.GUI_ENERGY
import net.ndrei.teslacorelib.localization.localizeModString

/**
 * Created by CF on 2017-06-28.
 */
class TeslaEnergyLevelPiece(left: Int, top: Int, private val energyStorage: EnergyStorage?) : BasicContainerGuiPiece(left, top, 18, 54) {
    private var displayType = EnergyDisplayType.TESLA

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        BasicTeslaGuiContainer.bindDefaultTexture(container)

        container.drawTexturedRect(this.left, this.top, 1, 189, this.width, this.height)

        if (this.energyStorage != null) {
            val power = (this.energyStorage.stored * (this.height - 6) / this.energyStorage.capacity).toInt()

            container.drawTexturedRect(this.left + 2, this.top + 2, 20, 191, this.width - 4, this.height - 4)
            container.drawTexturedRect(this.left + 3, this.top + 3 + this.height - 6 - power, 35, 192 + this.height - 6 - power, this.width - 6, power + 2)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && (this.energyStorage != null)) {
            val tick = this.energyStorage.lastTickEnergy
            val average = this.energyStorage.averageEnergyPerTick

            val energySystem = EnergyDisplayType.TESLA
            val lines = mutableListOf(
                localizeModString(MOD_ID, GUI_ENERGY, "stored energy") {
                    +TextFormatting.DARK_PURPLE
                },
                localizeModString(MOD_ID, GUI_ENERGY, "stored line 1") {
                    +TextFormatting.GRAY
                    +energySystem.makeLightTextComponent(this@TeslaEnergyLevelPiece.energyStorage.stored)
                },
                localizeModString(MOD_ID, GUI_ENERGY, "stored line 2") {
                    +TextFormatting.GRAY
                    +energySystem.makeDarkTextComponent(this@TeslaEnergyLevelPiece.energyStorage.capacity)
                },
                localizeModString(MOD_ID, GUI_ENERGY, "statistic") {
                    +energySystem.makeTextComponent(average, if (average < 0) TextFormatting.RED else TextFormatting.BLUE)
                    +energySystem.makeTextComponent(tick, if (tick < 0) TextFormatting.RED else TextFormatting.BLUE)
                }
            ).map { it.formattedText }
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }
}
