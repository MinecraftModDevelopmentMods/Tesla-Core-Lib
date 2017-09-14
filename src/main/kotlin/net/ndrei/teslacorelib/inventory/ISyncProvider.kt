package net.ndrei.teslacorelib.inventory

interface ISyncProvider {
    fun setSyncTarget(target: ISyncTarget, key: String?)
}
