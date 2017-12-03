package net.ndrei.teslacorelib.config

import net.minecraftforge.common.config.Configuration
import net.ndrei.teslacorelib.gui.EnergyDisplayType
import net.ndrei.teslacorelib.items.gears.CoreGearType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ConfigFlag(val description: String, val category: String = "flags", val default: Boolean = false)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ConfigFlags(val description: String, val category: String = "flags", vararg val default: String)

/**
 * Created by CF on 2017-07-13.
 */
@Suppress("MemberVisibilityCanPrivate")
object TeslaCoreLibConfig : GenericModConfigFlags() {
    @ConfigFlag(
        "Specifies if tesla machines are allowed to spawn items in world in case their output inventory is full.\n" +
            "Warning: some machines will cause the items to be lost if not spawned in the world (WIP).",
        default = true
    )
    const val MACHINES_SPAWN_ITEMS = "allowMachinesToSpawnItems"

    @ConfigFlag("Specifies if the metal sheet items will be registered or not.")
    const val REGISTER_SHEETS = "registerSheets"

    @ConfigFlag("Specifies if the gear items will be registered or not.", "flags.gears")
    const val REGISTER_GEARS = "registerGears"
    const val REGISTER_GEAR_TYPES = "registerGearTypes"

    @ConfigFlag("Specifies if the metal powder items will be registered or not.")
    const val REGISTER_POWDERS = "registerPowders"

    @ConfigFlag("Specifies if the creative test machines will be registered or not.")
    const val REGISTER_TEST_MACHINES = "registerTestMachines"

    @ConfigFlag("Specifies if the addon items will be registered or not.", "flags.addons")
    const val REGISTER_ADDONS = "registerAddons"
    @ConfigFlag("Specifies if the speed addon items will be registered or not.\nOnly makes sense if '$REGISTER_ADDONS' is set to true.", "flags.addons")
    const val REGISTER_SPEED_ADDONS = "registerSpeedAddons"
    @ConfigFlag("Specifies if the energy addon items will be registered or not.\nOnly makes sense if '$REGISTER_ADDONS' is set to true.", "flags.addons")
    const val REGISTER_ENERGY_ADDONS = "registerEnergyAddons"

    @ConfigFlag("Specifies if the simple tesla battery item will be registered or not.")
    const val REGISTER_BATTERY = "registerBattery"
    @ConfigFlag("Specifies if the machine case item will be registered or not.")
    const val REGISTER_MACHINE_CASE = "registerMachineCase"

    var energyDisplay: EnergyDisplayType
        get() = this.configuration.getString("energyDisplay", "gui", "Tesla",
            "Specifies the energy display type for the energy GUI piece.")
            .let {
                EnergyDisplayType.values().forEach { edt ->
                    if (edt.name.toLowerCase() == it.toLowerCase()) {
                        return@let edt
                    }
                }
                return EnergyDisplayType.TESLA
            }
        set(value) {
            this.configuration["gui", "energyDisplay", "rf"].set(value.name)
            this.checkIfConfigChanged()
        }

    fun allowMachinesToSpawnItems(): Boolean {
        return this.getFlag(MACHINES_SPAWN_ITEMS)
    }

    override fun loadConfig(config: Configuration) {
        super.loadConfig(config)

        this.readFlags(REGISTER_GEAR_TYPES, "Specify the type of gear to be registered.\nValid types are: ${CoreGearType.values().map { it.name.toLowerCase() }.joinToString(", ")}.",
            "flags.gears", "wood", "stone")
    }
}
