package net.ndrei.teslacorelib.gui

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.MOD_ID

@SideOnly(Side.CLIENT)
enum class GuiTexture(path: String): IGuiTexture {
    BASIC_MACHINES("textures/gui/basic-machine.png");

    override val resource = ResourceLocation(MOD_ID, path)
}
