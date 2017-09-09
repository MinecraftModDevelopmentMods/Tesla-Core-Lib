@file:Suppress("unused")
package net.ndrei.teslacorelib.utils

fun<E> MutableList<E>.alsoAdd(vararg thing: E) = this.also { it.addAll(thing) }
