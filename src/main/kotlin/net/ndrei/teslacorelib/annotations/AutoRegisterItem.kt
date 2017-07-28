package net.ndrei.teslacorelib.annotations

import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.items.ISelfRegisteringItem

/**
 * Created by CF on 2017-06-22.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterItem

object AutoRegisterItemHandler : BaseAnnotationHandler<Any>({ it, _, _ ->
    val registry = GameRegistry.findRegistry(Item::class.java)
    when (it) {
        is ISelfRegisteringItem -> it.registerItem(registry)
        is Item -> registry.register(it)
        else -> TeslaCoreLib.logger.warn("Annotated class can't be registered as an item: '${it.javaClass.canonicalName}'.")
    }
}, AutoRegisterItem::class, AutoRegisterBlock::class)
