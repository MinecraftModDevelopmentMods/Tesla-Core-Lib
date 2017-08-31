package net.ndrei.teslacorelib.items.sheets

import net.ndrei.teslacorelib.SheetRegistry
import net.ndrei.teslacorelib.annotations.AnnotationPreInitHandler
import net.ndrei.teslacorelib.annotations.BaseAnnotationHandler

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterSheet(vararg val configFlags: String)

@AnnotationPreInitHandler
@Suppress("unused")
object AutoRegisterSheetHandler : BaseAnnotationHandler<ColoredSheetItem>({ it, _, _ ->
    SheetRegistry.addMaterial(it.materialName, it)
}, AutoRegisterSheet::class)
