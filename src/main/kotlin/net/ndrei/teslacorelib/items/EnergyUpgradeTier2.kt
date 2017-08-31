package net.ndrei.teslacorelib.items

import net.ndrei.teslacorelib.ProxyLoadLevel
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem(TeslaCoreLibConfig.REGISTER_ADDONS, TeslaCoreLibConfig.REGISTER_ENERGY_ADDONS)
object EnergyUpgradeTier2 : EnergyUpgrade(2) {
    init {
        TeslaCoreLib.proxy.testLoadLevel(ProxyLoadLevel.ITEMS, this.javaClass.simpleName)
    }
}
