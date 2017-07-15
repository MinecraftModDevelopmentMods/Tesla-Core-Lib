package net.ndrei.teslacorelib.config

import net.minecraftforge.common.config.Configuration
import java.io.File

/**
 * Created by CF on 2017-07-13.
 */
class TeslaCoreLibConfig(configurationFile: File) {
    private val config: Configuration = Configuration(configurationFile)

    private var _allowMachinesToSpawnItems: Boolean = false

    init { this.update() }

    fun allowMachinesToSpawnItems(): Boolean {
        return this._allowMachinesToSpawnItems
    }

    private fun update() {
        try {
            this.config.load()
            this._allowMachinesToSpawnItems = this.config.getBoolean(
                    "allowMachinesToSpawnItems",
                    Configuration.CATEGORY_GENERAL,
                    true,
                    "Specifies if tesla machines are allowed to spawn items in world in case their output inventory is full.\nWarning: some machines will cause the items to be lost if not spawned in the world (WIP)."
            )
        } finally {
            if (this.config.hasChanged()) {
                this.config.save()
            }
        }
    }
}