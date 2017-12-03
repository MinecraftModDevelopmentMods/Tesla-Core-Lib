package net.ndrei.teslacorelib.gui

import net.minecraft.util.text.TextFormatting
import net.ndrei.teslacorelib.MOD_ID
import net.ndrei.teslacorelib.localization.GUI_ENERGY
import net.ndrei.teslacorelib.localization.localizeModString
import net.ndrei.teslacorelib.localization.makeTextComponent

enum class EnergyDisplayType(val energySystem: String,
                             val lightColor: TextFormatting, val darkColor: TextFormatting,
                             val emptyIcon: IGuiIcon, val fullIcon: IGuiIcon,
                             val workIcon: IGuiIcon,
                             val tesla: Float) {
    TESLA("Tesla", TextFormatting.AQUA, TextFormatting.DARK_AQUA, GuiIcon.ENERGY_EMPTY_TESLA, GuiIcon.ENERGY_FULL_TESLA, GuiIcon.ENERGY_WORK_TESLA, 1.0f)
    , RF("Redstone Flux", TextFormatting.RED, TextFormatting.DARK_RED, GuiIcon.ENERGY_EMPTY_RF, GuiIcon.ENERGY_FULL_RF, GuiIcon.ENERGY_WORK_RF, 1.0f)
    //,MJ("BuildCraft MJ", TextFormatting.AQUA, TextFormatting.DARK_AQUA, GuiIcon.ENERGY_EMPTY_MJ, GuiIcon.ENERGY_FULL_MJ, 10.0f, 227, 134)
    ;

    fun fromTesla(tesla: Long) =
        (tesla.toFloat() / this.tesla).toLong()

    fun makeTextComponent(format: TextFormatting? = null) =
        localizeModString(MOD_ID, GUI_ENERGY, this.energySystem) {
            if (format != null) {
                +format
            }
        }

    fun formatPower(power: Long) =
        String.format("%,d", Math.round(power.toDouble() / this.tesla.toDouble()))

    fun makeTextComponent(power: Long, format: TextFormatting? = null) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            if (format != null) {
                +format
            }
            +formatPower(power).makeTextComponent(format)
        }

    fun makeLightTextComponent(power: Long) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            +this@EnergyDisplayType.lightColor
            +formatPower(power).makeTextComponent(this@EnergyDisplayType.lightColor)
        }

    fun makeDarkTextComponent(power: Long) =
        localizeModString(MOD_ID, GUI_ENERGY, "${this.energySystem}_format") {
            +this@EnergyDisplayType.darkColor
            +formatPower(power).makeTextComponent(this@EnergyDisplayType.darkColor)
        }
}
