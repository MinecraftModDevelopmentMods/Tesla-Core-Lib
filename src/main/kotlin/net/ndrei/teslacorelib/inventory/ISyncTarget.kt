package net.ndrei.teslacorelib.inventory

interface ISyncTarget {
    fun partialSync(key: String)
    fun forceSync()
}