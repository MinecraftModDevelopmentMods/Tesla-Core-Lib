package net.ndrei.teslacorelib.items

import net.ndrei.teslacorelib.ProxyLoadLevel
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem(TeslaCoreLibConfig.REGISTER_ADDONS)
object BaseAddonItem
    : BaseAddon(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "base_addon") {
    init {
        TeslaCoreLib.proxy.testLoadLevel(ProxyLoadLevel.ITEMS, this.javaClass.simpleName)
    }
}
