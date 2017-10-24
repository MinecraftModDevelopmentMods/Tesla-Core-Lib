package net.ndrei.teslacorelib.config

import net.minecraftforge.common.config.Configuration

interface IModConfigFlags {
    fun setDefaultFlag(key: String, value: Boolean): Boolean
    fun getFlag(key: String): Boolean

    val configuration: Configuration
    fun checkIfConfigChanged()
}
