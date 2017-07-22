package net.ndrei.teslacorelib

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslacorelib.compatibility.RFPowerProxy
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.energy.systems.MJSystem
import net.ndrei.teslacorelib.energy.systems.MekanismSystem
import net.ndrei.teslacorelib.energy.systems.TeslaSystem
import net.ndrei.teslacorelib.items.TeslaWrench
import net.ndrei.teslacorelib.netsync.ITeslaCorePackets
import net.ndrei.teslacorelib.netsync.TeslaCorePackets
import org.apache.logging.log4j.Logger

/**
 * Created by CF on 2017-06-28.
 */
@Mod(modid = TeslaCoreLib.MODID, /*version = TeslaCoreLib.VERSION, */name = "Tesla Core Lib",
        dependencies = "required-after:forgelin;after:forge;after:${TeslaSystem.MODID};after:${RFPowerProxy.MODID};after:${MJSystem.MODID};after:${MekanismSystem.MODID}", useMetadata = true,
        modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
class TeslaCoreLib {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        TeslaCoreLib.logger = event.modLog
        TeslaCoreLib.config = TeslaCoreLibConfig(event.suggestedConfigurationFile)

        RFPowerProxy.isRFAvailable = Loader.isModLoaded(RFPowerProxy.MODID)

        TeslaCoreLib.proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        TeslaCoreLib.proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        TeslaCoreLib.proxy.postInit(event)
    }

    companion object {
        const val MODID = "teslacorelib"
//        const val VERSION = "@@VERSION@@"

        @Mod.Instance
        lateinit var instance: TeslaCoreLib

        @SidedProxy(clientSide = "net.ndrei.teslacorelib.ClientProxy", serverSide = "net.ndrei.teslacorelib.ServerProxy")
        lateinit var proxy: CommonProxy

        lateinit var logger: Logger

        lateinit var config: TeslaCoreLibConfig

        val network: ITeslaCorePackets = TeslaCorePackets(MODID)

        val creativeTab: CreativeTabs = object : CreativeTabs("tesla_core_lib") {
            override fun getIconItemStack(): ItemStack {
                return ItemStack(TeslaWrench)
            }

            override fun getTabIconItem(): ItemStack {
                return this.iconItemStack
            }
        }

        val isClientSide
            get() = net.minecraftforge.fml.common.FMLCommonHandler.instance().effectiveSide?.isClient ?: false

    }
}
