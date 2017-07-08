package net.ndrei.teslacorelib.compatibility

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by CF on 2017-07-08.
 */
fun java.lang.Class<*>.findDeclaredField(name: String): Field? {
    var field: Field? = null
    var cl: java.lang.Class<*>? = this

    while ((field == null) && (cl != null)) {
        try {
            field = cl.getDeclaredField(name)
        } catch(e: NoSuchFieldException) {
            field = null
            cl = cl.superclass
        }
    }

    return field
}

fun java.lang.Class<*>.findDeclaredMethod(name: String, vararg parameterTypes: java.lang.Class<*>): Method? {
    var field: Method? = null
    var cl: java.lang.Class<*>? = this

    while ((field == null) && (cl != null)) {
        try {
            field = cl.getDeclaredMethod(name, *parameterTypes)
        } catch(e: NoSuchMethodException) {
            field = null
            cl = cl.superclass
        }
    }

    return field
}