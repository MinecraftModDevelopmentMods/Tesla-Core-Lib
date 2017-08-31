package net.ndrei.teslacorelib.config

interface IModConfigFlagsProvider {
    val modConfigFlags: IModConfigFlags

    fun getFlag(key: String) = this.modConfigFlags.getFlag(key)
}
