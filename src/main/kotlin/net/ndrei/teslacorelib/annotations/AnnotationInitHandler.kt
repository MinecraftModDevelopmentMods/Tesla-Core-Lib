package net.ndrei.teslacorelib.annotations

import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AnnotationInitHandler(vararg val configFlags: String)

// because just two 'Handler's are never enough
object AnnotationInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationInitHandler::class)

fun processInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
    AnnotationInitHandlerHandlerHandler.process(asm, container)
}
