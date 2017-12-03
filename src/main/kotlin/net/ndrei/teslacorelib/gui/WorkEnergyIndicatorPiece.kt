package net.ndrei.teslacorelib.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.localization.GUI_WORK_ENERGY
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslacorelib.localization.makeTextComponent

/**
 * Created by CF on 2017-06-28.
 */
class WorkEnergyIndicatorPiece(private val provider: IWorkEnergyProvider?, left: Int, top: Int)
    : BasicContainerGuiPiece(left, top, 36, 4) {
    private val displayType get() = TeslaCoreLibConfig.energyDisplay

    override fun drawBackgroundLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, partialTicks: Float, mouseX: Int, mouseY: Int) {
        container.bindDefaultTexture()

        container.drawTexturedModalRect(guiX + this.left, guiY + this.top, 1, 245, this.width, this.height)
        if (this.provider != null) {
            val percent = this.provider.workEnergyStored.toFloat() / this.provider.workEnergyCapacity.toFloat()
            val width = Math.max(0, Math.min(this.width - 2, Math.round((this.width - 2) * percent)))
            if (width > 0) {
                container.drawTexturedModalRect(
                        guiX + this.left + 1, guiY + this.top + 1,
                        this.displayType.workIcon.left, this.displayType.workIcon.top, width, this.height - 2)
            }
        }
    }

    override fun drawForegroundTopLayer(container: BasicTeslaGuiContainer<*>, guiX: Int, guiY: Int, mouseX: Int, mouseY: Int) {
        if (super.isInside(container, mouseX, mouseY) && this.provider != null) {
            val energySystem = this.displayType
            val lines = (if (this@WorkEnergyIndicatorPiece.provider.hasWorkBuffer) mutableListOf(
                localizeModString(MOD_ID, GUI_WORK_ENERGY, "work energy buffer") {
                    +TextFormatting.DARK_PURPLE
                },
                localizeModString(MOD_ID, GUI_WORK_ENERGY, "work energy line 1") {
                    +TextFormatting.GRAY
                    +energySystem.makeLightTextComponent(this@WorkEnergyIndicatorPiece.provider.workEnergyStored)
                    +energySystem.makeDarkTextComponent(this@WorkEnergyIndicatorPiece.provider.workEnergyCapacity)
                },
                localizeModString(MOD_ID, GUI_WORK_ENERGY, "work energy line 2") {
                    +TextFormatting.GRAY
                    +energySystem.makeLightTextComponent(this@WorkEnergyIndicatorPiece.provider.workEnergyTick)
                },
                localizeModString(MOD_ID, GUI_WORK_ENERGY, "work energy line 3") {
                    +TextFormatting.GRAY
                    +(this@WorkEnergyIndicatorPiece.provider.workEnergyCapacity / this@WorkEnergyIndicatorPiece.provider.workEnergyTick)
                        .makeTextComponent(TextFormatting.WHITE)
                }
            ) else mutableListOf(
                localizeModString(MOD_ID, GUI_WORK_ENERGY, "waiting for work") {
                    +TextFormatting.DARK_RED
                }
            )).map { it.formattedText }

            container.drawTooltip(lines, mouseX - guiX, mouseY - guiY)
        }
    }
}
