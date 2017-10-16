package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.client.renderer.BufferBuilder

@FunctionalInterface
interface IDrawable {
    fun draw(buffer: BufferBuilder)
}