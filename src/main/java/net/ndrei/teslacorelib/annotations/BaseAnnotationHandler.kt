package net.ndrei.teslacorelib.annotations

import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.ndrei.teslacorelib.TeslaCoreLib
import kotlin.reflect.KClass

/**
 * Created by CF on 2017-06-29.
 */
abstract class BaseAnnotationHandler<in T> protected constructor(val handler: (thing: T, asm: ASMDataTable) -> Unit, vararg val annotations: KClass<*>) {
    fun process(asm: ASMDataTable) {
        val all = mutableSetOf<ASMDataTable.ASMData>()
        annotations.forEach {
            all.addAll(asm.getAll(it.java.canonicalName))
        }

        all.sortedBy { it.className }.forEach {
            val c = try {
                Class.forName(it.className)
            } catch (e: ClassNotFoundException) {
                TeslaCoreLib.logger.error("Annotated class '${it.className}' not found!", e)
                null
            }

            if (c != null) {
                val instance = try {
                    @Suppress("UNCHECKED_CAST")
                    (if (c.kotlin.objectInstance != null) c.kotlin.objectInstance else c.getConstructor()?.newInstance()) as T
                } catch (e: ClassCastException) {
                    TeslaCoreLib.logger.error("Annotated class '${it.className}' could not be casted to desired type!", e)
                    null
                }

                if (instance != null) {
                    this.handler(instance, asm)
                }
            }
        }
    }
}