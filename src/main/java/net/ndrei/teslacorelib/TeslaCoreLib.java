package net.ndrei.teslacorelib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.gui.TeslaCoreGuiProxy;
import net.ndrei.teslacorelib.items.BaseAddonItem;
import net.ndrei.teslacorelib.items.MachineCaseItem;
import net.ndrei.teslacorelib.items.TeslaBattery;
import net.ndrei.teslacorelib.items.TeslaWrench;
import net.ndrei.teslacorelib.netsync.ITeslaCorePackets;
import net.ndrei.teslacorelib.netsync.TeslaCorePackets;
import net.ndrei.teslacorelib.test.CreativeGeneratorBlock;
import net.ndrei.teslacorelib.test.TeslaCoreUITestBlock;
import org.apache.logging.log4j.Logger;

@Mod(modid = TeslaCoreLib.MODID, version = TeslaCoreLib.VERSION, name = "Tesla Core Lib", dependencies = "after:tesla", useMetadata = true)
public class TeslaCoreLib
{
    public static final String MODID = "teslacorelib";
    public static final String VERSION = "@@VERSION@@";

    @Mod.Instance
    @SuppressWarnings("unused")
    public static TeslaCoreLib instance;

    public static Logger logger;

    public static ITeslaCorePackets network = new TeslaCorePackets(MODID);

    public static CreativeTabs creativeTab =  new CreativeTabs("Tesla Core Lib") {
        @Override
        public ItemStack getIconItemStack()
        {
            return new ItemStack(TeslaCoreLib.wrench);
        }

        @Override
        public ItemStack getTabIconItem() { return this.getIconItemStack(); }
    };

    public static TeslaWrench wrench;
    public static TeslaBattery battery;
    public static BaseAddonItem baseAddon;
    public static MachineCaseItem machineCase;

    public static TeslaCoreUITestBlock testBlock;
    public static CreativeGeneratorBlock generatorBlock;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event){
        TeslaCoreLib.logger = event.getModLog();

        GameRegistry.register(TeslaCoreLib.wrench = new TeslaWrench());
        GameRegistry.register(TeslaCoreLib.battery = new TeslaBattery());
        GameRegistry.register(TeslaCoreLib.baseAddon = new BaseAddonItem());
        GameRegistry.register(TeslaCoreLib.machineCase = new MachineCaseItem());
        TeslaCoreCapabilities.register();

        TeslaCoreLib.testBlock = new TeslaCoreUITestBlock();
        TeslaCoreLib.testBlock.register();
        TeslaCoreLib.testBlock.setCreativeTab(TeslaCoreLib.creativeTab);

        TeslaCoreLib.generatorBlock = new CreativeGeneratorBlock();
        TeslaCoreLib.generatorBlock.register();
        TeslaCoreLib.generatorBlock.setCreativeTab(TeslaCoreLib.creativeTab);

        if (event.getSide() == Side.CLIENT) {
            TeslaCoreLib.wrench.registerRenderer();
            TeslaCoreLib.battery.registerRenderer();
            TeslaCoreLib.baseAddon.registerRenderer();
            TeslaCoreLib.machineCase.registerRenderer();

            TeslaCoreLib.testBlock.registerRenderer();
            TeslaCoreLib.generatorBlock.registerRenderer();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(TeslaCoreLib.instance, new TeslaCoreGuiProxy());
    }
}
