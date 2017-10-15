package net.ndrei.teslacorelib.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.ResourceLocation

interface IGuiTexture {
    val resource: ResourceLocation

    val sprite: TextureAtlasSprite
        get() = Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(this.resource.toString())
            ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite

    fun bind(container: BasicTeslaGuiContainer<*>?) {
        (container?.mc ?: Minecraft.getMinecraft()).textureManager.bindTexture(this.resource)
    }
}