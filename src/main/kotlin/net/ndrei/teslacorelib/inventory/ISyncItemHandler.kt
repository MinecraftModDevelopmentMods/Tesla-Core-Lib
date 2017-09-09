package net.ndrei.teslacorelib.inventory

interface ISyncItemHandler {
    fun setSyncTarget(target: ISyncTarget, key: String?)
}