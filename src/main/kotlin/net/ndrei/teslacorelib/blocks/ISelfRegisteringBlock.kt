package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.items.ISelfRegisteringItem

/**
 * Created by CF on 2017-07-15.
 */
interface ISelfRegisteringBlock: ISelfRegisteringItem {
    fun registerBlock(blockRegistry: IForgeRegistry<Block>)
}