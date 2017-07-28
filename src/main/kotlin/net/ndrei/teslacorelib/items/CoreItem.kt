package net.ndrei.teslacorelib.items

import net.ndrei.teslacorelib.ProxyLoadLevel
import net.ndrei.teslacorelib.TeslaCoreLib

abstract class CoreItem(registryName: String)
    : RegisteredItem(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, registryName) {
    init {
        TeslaCoreLib.proxy.testLoadLevel(ProxyLoadLevel.ITEMS, this.javaClass.simpleName)
    }
}