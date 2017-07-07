package net.ndrei.teslacorelib.items.gears

import net.ndrei.teslacorelib.GearRegistry
import net.ndrei.teslacorelib.annotations.AnnotationPreInitHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterGear

@AnnotationPreInitHandler
@Suppress("unused")
object AutoRegisterGearHandler : BaseAnnotationHandler<BaseGearItem>({ it, _, _ ->
    GearRegistry.addMaterial(it.materialName, it)
}, AutoRegisterGear::class)