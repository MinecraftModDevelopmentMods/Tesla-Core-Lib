package net.ndrei.teslacorelib.items

import net.ndrei.teslacorelib.ProxyLoadLevel
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object EnergyUpgradeTier2 : EnergyUpgrade(2) {
    init {
        TeslaCoreLib.proxy.testLoadLevel(ProxyLoadLevel.ITEMS, this.javaClass.simpleName)
    }
}