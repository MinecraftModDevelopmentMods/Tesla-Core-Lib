package net.ndrei.teslacorelib.items

import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry

interface ISelfRegisteringItem {
    fun registerItem(registry: IForgeRegistry<Item>)
}