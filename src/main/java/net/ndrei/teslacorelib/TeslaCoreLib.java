package net.ndrei.teslacorelib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.gui.TeslaCoreGuiProxy;
import net.ndrei.teslacorelib.items.*;
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

    public static CreativeTabs creativeTab =  new CreativeTabs("tesla_core_lib") {
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

    public static GearWoodItem gearWood;
    public static GearStoneItem gearStone;
    public static GearIronItem gearIron;
    public static GearGoldItem gearGold;
    public static GearDiamondItem gearDiamond;

    public static EnergyUpgradeTier1 energyUpgradeTier1;
    public static EnergyUpgradeTier2 energyUpgradeTier2;
    public static SpeedUpgradeTier1 speedUpgradeTier1;
    public static SpeedUpgradeTier2 speedUpgradeTier2;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event){
        TeslaCoreLib.logger = event.getModLog();

        (TeslaCoreLib.gearWood = new GearWoodItem()).register();
        (TeslaCoreLib.gearStone = new GearStoneItem()).register();
        (TeslaCoreLib.gearIron = new GearIronItem()).register();
        (TeslaCoreLib.gearGold = new GearGoldItem()).register();
        (TeslaCoreLib.gearDiamond = new GearDiamondItem()).register();

        (TeslaCoreLib.wrench = new TeslaWrench()).register();
        (TeslaCoreLib.battery = new TeslaBattery()).register();
        (TeslaCoreLib.baseAddon = new BaseAddonItem()).register();
        (TeslaCoreLib.machineCase = new MachineCaseItem()).register();
        TeslaCoreCapabilities.register();

        (TeslaCoreLib.energyUpgradeTier1 = new EnergyUpgradeTier1()).register();
        (TeslaCoreLib.energyUpgradeTier2 = new EnergyUpgradeTier2()).register();
        (TeslaCoreLib.speedUpgradeTier1 = new SpeedUpgradeTier1()).register();
        (TeslaCoreLib.speedUpgradeTier2 = new SpeedUpgradeTier2()).register();

        TeslaCoreLib.testBlock = new TeslaCoreUITestBlock();
        TeslaCoreLib.testBlock.register();
        TeslaCoreLib.testBlock.setCreativeTab(TeslaCoreLib.creativeTab);

        TeslaCoreLib.generatorBlock = new CreativeGeneratorBlock();
        TeslaCoreLib.generatorBlock.register();
        TeslaCoreLib.generatorBlock.setCreativeTab(TeslaCoreLib.creativeTab);

        if (event.getSide() == Side.CLIENT) {
            TeslaCoreLib.gearWood.registerRenderer();
            TeslaCoreLib.gearStone.registerRenderer();
            TeslaCoreLib.gearIron.registerRenderer();
            TeslaCoreLib.gearGold.registerRenderer();
            TeslaCoreLib.gearDiamond.registerRenderer();

            TeslaCoreLib.wrench.registerRenderer();
            TeslaCoreLib.battery.registerRenderer();
            TeslaCoreLib.baseAddon.registerRenderer();
            TeslaCoreLib.machineCase.registerRenderer();

            TeslaCoreLib.energyUpgradeTier1.registerRenderer();
            TeslaCoreLib.energyUpgradeTier2.registerRenderer();
            TeslaCoreLib.speedUpgradeTier1.registerRenderer();
            TeslaCoreLib.speedUpgradeTier2.registerRenderer();

            TeslaCoreLib.testBlock.registerRenderer();
            TeslaCoreLib.generatorBlock.registerRenderer();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(TeslaCoreLib.instance, new TeslaCoreGuiProxy());
    }
}
