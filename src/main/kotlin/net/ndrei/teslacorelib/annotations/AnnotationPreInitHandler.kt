package net.ndrei.teslacorelib.annotations

import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AnnotationPreInitHandler(vararg val configFlags: String)

// because just two 'Handler's are never enough
object AnnotationPreInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationPreInitHandler::class)

fun processPreInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
    AutoRegisterFluidHandler.process(asm, container)
    AnnotationPreInitHandlerHandlerHandler.process(asm, container)
}
