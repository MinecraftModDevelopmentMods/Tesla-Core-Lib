package net.ndrei.teslacorelib.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.inventory.EnergyStorage
import net.ndrei.teslacorelib.localization.GUI_ENERGY
import net.ndrei.teslacorelib.localization.localizeModString

/**
 * Created by CF on 2017-06-28.
 */
class TeslaEnergyLevelPiece(left: Int, top: Int, private val energyStorage: EnergyStorage?) : BasicContainerGuiPiece(left, top, 18, 54) {
    private val displayType get() = TeslaCoreLibConfig.energyDisplay

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        BasicTeslaGuiContainer.bindDefaultTexture(container)

        container.drawTexturedRect(this.left, this.top, 1, 189, this.width, this.height)
        container.drawTexturedRect(this.left + 2, this.top + 2, 20, 191, this.width - 4, this.height - 4)

        if (this.energyStorage != null) {
            val stored = this.displayType.fromTesla(this.energyStorage.stored)
            val capacity = this.displayType.fromTesla(this.energyStorage.capacity)
            val power = (stored * this.displayType.fullIcon.height / capacity).toInt()

            container.drawTexturedRect(this.left + 3, this.top + 3,
                //20, 191,
                this.displayType.emptyIcon.left, this.displayType.emptyIcon.top,
                this.displayType.emptyIcon.width, this.displayType.emptyIcon.height)
            container.drawTexturedRect(this.left + 3, this.top + 3 + this.displayType.fullIcon.height - power,
                this.displayType.fullIcon.left, this.displayType.fullIcon.top + this.displayType.fullIcon.height - power,
                this.displayType.fullIcon.width, power)
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && (this.energyStorage != null)) {
            val tick = this.energyStorage.lastTickEnergy
            val average = this.energyStorage.averageEnergyPerTick

            val lines = mutableListOf(
                localizeModString(MOD_ID, GUI_ENERGY, "stored energy") {
                    +TextFormatting.DARK_PURPLE
                },
                localizeModString(MOD_ID, GUI_ENERGY, "stored line 1") {
                    +TextFormatting.GRAY
                    +this@TeslaEnergyLevelPiece.displayType.makeLightTextComponent(this@TeslaEnergyLevelPiece.energyStorage.stored)
                },
                localizeModString(MOD_ID, GUI_ENERGY, "stored line 2") {
                    +TextFormatting.GRAY
                    +this@TeslaEnergyLevelPiece.displayType.makeDarkTextComponent(this@TeslaEnergyLevelPiece.energyStorage.capacity)
                },
                localizeModString(MOD_ID, GUI_ENERGY, "statistic") {
                    +this@TeslaEnergyLevelPiece.displayType.makeTextComponent(average, if (average < 0) TextFormatting.RED else TextFormatting.BLUE)
                    +this@TeslaEnergyLevelPiece.displayType.makeTextComponent(tick, if (tick < 0) TextFormatting.RED else TextFormatting.BLUE)
                }
            ).map { it.formattedText }
            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }

    override fun mouseClicked(container: BasicTeslaGuiContainer<*>, mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(container, mouseX, mouseY, mouseButton)

        if (this.isInside(container, mouseX, mouseY) && TeslaCoreLibConfig.getFlag(TeslaCoreLibConfig.ALLOW_ENERGY_DISPLAY_CHANGE)) {
            TeslaCoreLibConfig.energyDisplay = EnergyDisplayType.values()[
                (TeslaCoreLibConfig.energyDisplay.ordinal + 1) % EnergyDisplayType.values().size
                ]
        }
    }
}
