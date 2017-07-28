package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraftforge.fml.common.registry.GameRegistry
import net.ndrei.teslacorelib.blocks.ISelfRegisteringBlock

/**
 * Created by CF on 2017-06-22.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterBlock

object AutoRegisterBlockHandler: BaseAnnotationHandler<Block>({ it, _, _ ->
    val blockRegistry = GameRegistry.findRegistry(Block::class.java)
    when (it) {
        is ISelfRegisteringBlock -> it.registerBlock(blockRegistry)
        else -> blockRegistry.register(it)
    }
}, AutoRegisterBlock::class)
