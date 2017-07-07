package net.ndrei.teslacorelib.annotations

import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry
import net.ndrei.teslacorelib.items.RegisteredItem

/**
 * Created by CF on 2017-06-22.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterItem

object AutoRegisterItemHandler : BaseAnnotationHandler<Item>({ it, _, _ ->
    val registry = GameRegistry.findRegistry(Item::class.java)
    when (it) {
        is RegisteredItem -> it.register(registry)
        else -> registry.register(it)
    }
}, AutoRegisterItem::class)
