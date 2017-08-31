package net.ndrei.teslacorelib.config

interface IModConfigFlags {
    fun setDefaultFlag(key: String, value: Boolean): Boolean
    fun getFlag(key: String): Boolean
}
