package net.ndrei.teslacorelib.items.powders

import net.ndrei.teslacorelib.PowderRegistry
import net.ndrei.teslacorelib.annotations.AnnotationPreInitHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterPowder(vararg val configFlags: String)

@AnnotationPreInitHandler
@Suppress("unused")
object AutoRegisterPowderHandler : BaseAnnotationHandler<ColoredPowderItem>({ it, _, _ ->
    PowderRegistry.addMaterial(it.materialName, it)
}, AutoRegisterPowder::class)
