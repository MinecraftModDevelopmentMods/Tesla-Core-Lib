package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry

/**
 * Created by CF on 2017-07-15.
 */
interface ISelfRegisteringBlock {
    fun register(blockRegistry: IForgeRegistry<Block>, itemRegistry: IForgeRegistry<Item>)
}