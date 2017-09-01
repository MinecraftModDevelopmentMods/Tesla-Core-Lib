package net.ndrei.teslacorelib.config

import net.minecraftforge.common.config.Configuration
import java.io.File

abstract class GenericModConfigFlags: IModConfigFlags {
    private val defaults = mutableMapOf<String, Boolean>()
    private val map = mutableMapOf<String, Boolean>()

    private lateinit var config: Configuration

    fun init(configurationFile: File) {
        this.config = Configuration(configurationFile)
        this.update()
    }

    override fun setDefaultFlag(key: String, value: Boolean): Boolean {
        val k = key.normalize()
        this.defaults[k] = value || this.defaults.getOrDefault(k, false)
        return this.defaults.getOrDefault(k, false)
    }

    private fun String?.normalize() = (this ?: "").toLowerCase()

    fun getDefaultFlag(key: String, defaultValue: Boolean = false) = this.defaults.getOrDefault(key.normalize(), defaultValue)

    fun setFlag(key: String, value: Boolean) {
        this.map[key.normalize()] = value
    }

    override fun getFlag(key: String) = this.map.getOrDefault(key.normalize(), false)

    protected fun readFlag(key: String, description: String, category: String = "flags", defaultValue: Boolean = false) {
        val k = key.normalize()
        this.setFlag(k,
            this.config.getBoolean(key, category, this.getDefaultFlag(k, defaultValue), description + "\n")
        )
    }

    protected fun readFlags(key: String, description: String, category: String = "flags", vararg defaultValues: String) {
        val k = key.normalize()
        val defaults = mutableListOf(*defaultValues)
        this.defaults
            .filterKeys { it.startsWith("$k#") }
            .mapKeys { (it, _) ->  it.substring("$k#".length) }
            .filterValues { it }
            .mapTo(defaults) { it.key }
        val list = this.config.getStringList(key, category, defaults.toTypedArray(), description + "\n")
        list.forEach { this.setFlag("$key#$it".normalize(), true) }
    }

    fun update() {
        try {
            this.config.load()

            this.loadConfig(config)
        } finally {
            if (this.config.hasChanged()) {
                this.config.save()
            }
        }
    }

    abstract protected fun loadConfig(config: Configuration)
}
