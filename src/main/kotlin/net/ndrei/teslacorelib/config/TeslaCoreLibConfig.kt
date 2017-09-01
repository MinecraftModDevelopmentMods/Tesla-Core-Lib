package net.ndrei.teslacorelib.config

import net.minecraftforge.common.config.Configuration
import net.ndrei.teslacorelib.items.gears.CoreGearType

/**
 * Created by CF on 2017-07-13.
 */
object TeslaCoreLibConfig : GenericModConfigFlags() {
    const val MACHINES_SPAWN_ITEMS = "allowMachinesToSpawnItems"
    const val REGISTER_SHEETS = "registerSheets"
    const val REGISTER_GEARS = "registerGears"
    const val REGISTER_GEAR_TYPES = "registerGearTypes"
    const val REGISTER_POWDERS = "registerPowders"
    const val REGISTER_TEST_MACHINES = "registerTestMachines"

    const val REGISTER_ADDONS = "registerAddons"
    const val REGISTER_SPEED_ADDONS = "registerSpeedAddons"
    const val REGISTER_ENERGY_ADDONS = "registerEnergyAddons"

    const val REGISTER_BATTERY = "registerBattery"
    const val REGISTER_MACHINE_CASE = "registerMachineCase"

    fun allowMachinesToSpawnItems(): Boolean {
        return this.getFlag(MACHINES_SPAWN_ITEMS)
    }

    override fun loadConfig(config: Configuration) {
        this.readFlag(MACHINES_SPAWN_ITEMS,
            "Specifies if tesla machines are allowed to spawn items in world in case their output inventory is full.\nWarning: some machines will cause the items to be lost if not spawned in the world (WIP).",
            defaultValue = true)

        this.readFlag(REGISTER_SHEETS, "Specifies if the metal sheet items will be registered or not.")
        this.readFlag(REGISTER_GEARS, "Specifies if the gear items will be registered or not.",
            "flags.gears")
        this.readFlags(REGISTER_GEAR_TYPES, "Specify the type of gear to be registered.\nValid types are: ${CoreGearType.values().map { it.name.toLowerCase() }.joinToString(", ")}.",
            "flags.gears", "wood", "stone")
        this.readFlag(REGISTER_POWDERS, "Specifies if the metal powder items will be registered or not.")

        this.readFlag(REGISTER_ADDONS, "Specifies if the addon items will be registered or not.",
            "flags.addons")
        this.readFlag(REGISTER_SPEED_ADDONS, "Specifies if the speed addon items will be registered or not.\nOnly makes sense if '$REGISTER_ADDONS' is set to true.",
            "flags.addons", true)
        this.readFlag(REGISTER_ENERGY_ADDONS, "Specifies if the energy addon items will be registered or not.\nOnly makes sense if '$REGISTER_ADDONS' is set to true.",
            "flags.addons", true)

        this.readFlag(REGISTER_TEST_MACHINES, "Specifies if the creative test machines will be registered or not.")

        this.readFlag(REGISTER_BATTERY, "Specifies if the simple tesla battery item will be registered or not.")
        this.readFlag(REGISTER_MACHINE_CASE, "Specifies if the machine case item will be registered or not.")
    }
}
