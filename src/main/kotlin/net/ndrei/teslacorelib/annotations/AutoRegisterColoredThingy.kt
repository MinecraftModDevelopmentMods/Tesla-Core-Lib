package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.compatibility.IBlockColorDelegate
import net.ndrei.teslacorelib.compatibility.IItemColorDelegate

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterColoredThingy(vararg val configFlags: String)

@Suppress("unused")
@AnnotationPostInitHandler
object AutoRegisterColoredThingyHandler: BaseAnnotationHandler<Any>({ it, _, _ ->
    if (TeslaCoreLib.isClientSide) {
        when (it) {
            is IItemColorDelegate -> when (it) {
                is Item -> Minecraft.getMinecraft().itemColors.registerItemColorHandler({ s: ItemStack, t: Int -> it.getColorFromItemStack(s, t) }, arrayOf<Item>(it))
                is Block -> Minecraft.getMinecraft().itemColors.registerItemColorHandler({ s: ItemStack, t: Int -> it.getColorFromItemStack(s, t) }, arrayOf<Block>(it))
                else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an Item nor a Block.")
            }
            is IBlockColorDelegate -> Minecraft.getMinecraft().blockColors.registerBlockColorHandler(
                    { state: IBlockState, worldIn: IBlockAccess?, pos: BlockPos?, tintIndex: Int -> it.colorMultiplier(state, worldIn, pos, tintIndex) },
                        arrayOf(it as Block))
            else -> TeslaCoreLib.logger.warn("[ColoredThingyHandler] Not sure what '${it.javaClass.canonicalName}' is but it is neither an IItemColorDelegate nor a IBlockColorDelegate.")
        }
    }
}, AutoRegisterColoredThingy::class)
