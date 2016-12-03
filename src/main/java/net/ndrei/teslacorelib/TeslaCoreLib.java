package net.ndrei.teslacorelib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.netsync.ITeslaCorePackets;
import net.ndrei.teslacorelib.netsync.TeslaCorePackets;
import org.apache.logging.log4j.Logger;

@Mod(modid = TeslaCoreLib.MODID, version = TeslaCoreLib.VERSION, name = "Tesla Core Lib", dependencies = "after:tesla", useMetadata = true)
public class TeslaCoreLib
{
    public static final String MODID = "teslacorelib";
    public static final String VERSION = "0.0.1";

    @Mod.Instance
    @SuppressWarnings("unused")
    public static TeslaCoreLib instance;

    public static Logger logger;

    public static ITeslaCorePackets network = new TeslaCorePackets(MODID);

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event){
        TeslaCoreLib.logger = event.getModLog();

        TeslaCoreCapabilities.register();
    }
}
