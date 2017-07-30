package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World

interface TESRProxy {
    val rendererDispatcher: TileEntityRendererDispatcher?
    val world: World
    val fontRenderer: FontRenderer

    fun setLightmapDisabled(disabled: Boolean)
    fun bindTexture(location: ResourceLocation)
}