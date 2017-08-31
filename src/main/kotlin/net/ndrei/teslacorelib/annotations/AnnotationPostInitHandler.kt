package net.ndrei.teslacorelib.annotations

import net.minecraftforge.fml.common.ModContainer
import net.minecraftforge.fml.common.discovery.ASMDataTable

/**
 * Created by CF on 2017-06-29.
 */
@Target(AnnotationTarget.CLASS)
annotation class AnnotationPostInitHandler(vararg val configFlags: String)

// because just two 'Handler's are never enough
object AnnotationPostInitHandlerHandlerHandler: BaseAnnotationHandler<BaseAnnotationHandler<*>>({ it, asm, container ->
    it.process(asm, container)
}, AnnotationPostInitHandler::class)

fun processPostInitAnnotations(asm: ASMDataTable, container: ModContainer?) {
    AnnotationPostInitHandlerHandlerHandler.process(asm, container)
}
