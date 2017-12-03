package net.ndrei.teslacorelib.gui

enum class GuiIcon(override val texture: GuiTexture, override val left: Int, override val top: Int, override val width: Int, override val height: Int): IGuiIcon {
    PAUSE(GuiTexture.BASIC_MACHINES, 225, 215, 5, 6),
    PLAY(GuiTexture.BASIC_MACHINES, 225, 200, 5, 8),
    LOCK_CLOSE(GuiTexture.BASIC_MACHINES, 167, 212, 8, 10),
    LOCK_OPEN(GuiTexture.BASIC_MACHINES, 203, 230, 8, 10),
    LOCK_GRAY(GuiTexture.BASIC_MACHINES, 221, 230, 8, 10),
    SMALL_BUTTON(GuiTexture.BASIC_MACHINES, 110, 210, 14, 14),
    SMALL_BUTTON_HOVER(GuiTexture.BASIC_MACHINES, 128, 210, 14, 14),

    ENERGY_EMPTY_TESLA(GuiTexture.BASIC_MACHINES, 21, 192, 12, 48),
    ENERGY_EMPTY_RF(GuiTexture.BASIC_MACHINES, 199, 134, 12, 48),
    ENERGY_EMPTY_MJ(GuiTexture.BASIC_MACHINES, 225, 134, 12, 48),
    ENERGY_FULL_TESLA(GuiTexture.BASIC_MACHINES, 35, 192, 12, 48),
    ENERGY_FULL_RF(GuiTexture.BASIC_MACHINES, 212, 134, 12, 48),
    ENERGY_FULL_MJ(GuiTexture.BASIC_MACHINES, 238, 134, 12, 48),
    ENERGY_WORK_TESLA(GuiTexture.BASIC_MACHINES, 2, 251, 34, 2),
    ENERGY_WORK_RF(GuiTexture.BASIC_MACHINES, 199, 131, 34, 2),
    ENERGY_WORK_MJ(GuiTexture.BASIC_MACHINES, 199, 183, 34, 2),
    ;
}
