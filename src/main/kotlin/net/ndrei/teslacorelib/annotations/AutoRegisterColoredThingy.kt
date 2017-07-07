package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IBlockColor
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.ndrei.teslacorelib.TeslaCoreLib

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterColoredThingy

@Suppress("unused")
@AnnotationPostInitHandler
object AutoRegisterColoredThingyHandler: BaseAnnotationHandler<Any>({ it, _, _ ->
    if (TeslaCoreLib.isClientSide) {
        when (it) {
            is IItemColor -> when (it) {
                is Item -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(it, it)
                is Block -> Minecraft.getMinecraft().itemColors.registerItemColorHandler(it, it)
                else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an Item nor a Block.")
            }
            is IBlockColor -> Minecraft.getMinecraft().blockColors.registerBlockColorHandler(it, it as Block)
            else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an IItemColor nor a IBlockColor.")
        }
    }
}, AutoRegisterColoredThingy::class)
