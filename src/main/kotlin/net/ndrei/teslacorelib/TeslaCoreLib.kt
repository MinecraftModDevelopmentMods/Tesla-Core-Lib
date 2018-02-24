package net.ndrei.teslacorelib

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLConstructionEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.ndrei.teslacorelib.compatibility.RFPowerProxy
import net.ndrei.teslacorelib.config.IModConfigFlagsProvider
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig
import net.ndrei.teslacorelib.config.TheGuiFactory
import net.ndrei.teslacorelib.energy.systems.MJSystem
import net.ndrei.teslacorelib.energy.systems.MekanismSystem
import net.ndrei.teslacorelib.energy.systems.TeslaSystem
import net.ndrei.teslacorelib.items.TeslaWrench
import net.ndrei.teslacorelib.netsync.ITeslaCorePackets
import net.ndrei.teslacorelib.netsync.TeslaCorePackets
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Created by CF on 2017-06-28.
 */
@Mod(modid = MOD_ID, version = MOD_VERSION, name = MOD_NAME,
    acceptedMinecraftVersions = MOD_MC_VERSION,
    dependencies = "${MOD_DEPENDENCIES}after:${TeslaSystem.MODID};after:${RFPowerProxy.MODID};after:${MJSystem.MODID};after:${MekanismSystem.MODID}",
    useMetadata = true, modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    guiFactory = TheGuiFactory.CLASS_NAME,
    certificateFingerprint = MOD_SIGN_FINGERPRINT
    )
object TeslaCoreLib : IModConfigFlagsProvider {
    @SidedProxy(clientSide = "net.ndrei.teslacorelib.ClientProxy", serverSide = "net.ndrei.teslacorelib.ServerProxy")
    lateinit var proxy: CommonProxy
    lateinit var logger: Logger

    val config = TeslaCoreLibConfig
    override val modConfigFlags = this.config

    // just as a 'proxy' for old code
    @Deprecated("Use the global constant MOD_ID instead.")
    const val MODID = MOD_ID // TODO: remove this

    val network: ITeslaCorePackets = TeslaCorePackets(MOD_ID)

    val creativeTab: CreativeTabs = object : CreativeTabs("tesla_core_lib") {
        override fun getTabIconItem() = ItemStack(TeslaWrench)
    }

    val isClientSide
        get() = net.minecraftforge.fml.common.FMLCommonHandler.instance().effectiveSide?.isClient ?: false

    @Mod.EventHandler
    fun construct(event: FMLConstructionEvent) {
        this.logger = LogManager.getLogger(Loader.instance().activeModContainer()!!.modId)

        this.proxy.construction(event)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        this.config.init(event.suggestedConfigurationFile)
        RFPowerProxy.isRFAvailable = Loader.isModLoaded(RFPowerProxy.MODID)

        this.proxy.preInit(event)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        this.proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        this.proxy.postInit(event)
    }
}
