package net.ndrei.teslacorelib.annotations

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry
import net.ndrei.teslacorelib.blocks.RegisteredBlock

/**
 * Created by CF on 2017-06-22.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterBlock

object AutoRegisterBlockHandler: BaseAnnotationHandler<Block>({ it, _, _ ->
    val itemRegistry = GameRegistry.findRegistry(Item::class.java)
    val blockRegistry = GameRegistry.findRegistry(Block::class.java)
    when (it) {
        is RegisteredBlock -> it.register(blockRegistry, itemRegistry)
        else -> {
            blockRegistry.register(it)
            val item = ItemBlock(it)
            item.registryName = it.registryName
            itemRegistry.register(item)
        }
    }
}, AutoRegisterBlock::class)
