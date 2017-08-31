package net.ndrei.teslacorelib.config

open class GenericModConfigFlags: IModConfigFlags {
    private val defaults = mutableMapOf<String, Boolean>()
    private val map = mutableMapOf<String, Boolean>()

    override fun setDefaultFlag(key: String, value: Boolean): Boolean {
        this.defaults[key] = value || this.defaults.getOrDefault(key, false)
        return this.defaults.getOrDefault(key, false)
    }

    fun getDefaultFlag(key: String, defaultValue: Boolean = false) = this.defaults.getOrDefault(key, defaultValue)

    fun setFlag(key: String, value: Boolean) {
        this.map[key] = value
    }

    override fun getFlag(key: String) = this.map.getOrDefault(key, false)
}
