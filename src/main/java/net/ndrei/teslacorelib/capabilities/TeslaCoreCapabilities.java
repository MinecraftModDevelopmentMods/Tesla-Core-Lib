package net.ndrei.teslacorelib.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.ndrei.teslacorelib.capabilities.container.IGuiContainerProvider;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.capabilities.hud.IHudInfoProvider;
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig;
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.items.TeslaWrench;

import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
public class TeslaCoreCapabilities {
    @CapabilityInject(IHudInfoProvider.class)
    public static Capability<IHudInfoProvider> CAPABILITY_HUD_INFO = null;

    @CapabilityInject(ITeslaWrenchHandler.class)
    public static Capability<ITeslaWrenchHandler> CAPABILITY_WRENCH = null;

//    @CapabilityInject(ISidedItemHandlerConfig.class)
//    public static Capability<ISidedItemHandlerConfig> CAPABILITY_SIDED_CONFIG = null;

    @CapabilityInject(ISidedItemHandlerConfig.class)
    public static Capability<IGuiContainerProvider> CAPABILITY_GUI_CONTAINER = null;

    static class CapabilityHudInfoProvider<T extends IHudInfoProvider> implements Capability.IStorage<IHudInfoProvider> {
        @Override
        public NBTBase writeNBT (Capability<IHudInfoProvider> capability, IHudInfoProvider instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT (Capability<IHudInfoProvider> capability, IHudInfoProvider instance, EnumFacing side, NBTBase nbt) {
        }
    }

    static class CapabilityWrenchHandler<T extends ITeslaWrenchHandler> implements Capability.IStorage<ITeslaWrenchHandler> {
        @Override
        public NBTBase writeNBT (Capability<ITeslaWrenchHandler> capability, ITeslaWrenchHandler instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT (Capability<ITeslaWrenchHandler> capability, ITeslaWrenchHandler instance, EnumFacing side, NBTBase nbt) {
        }
    }

//    static class CapabilitySidedConfig<T extends ISidedItemHandlerConfig> implements Capability.IStorage<ISidedItemHandlerConfig> {
//        @Override
//        public NBTBase writeNBT (Capability<ISidedItemHandlerConfig> capability, ISidedItemHandlerConfig instance, EnumFacing side) {
//            return null;
//        }
//
//        @Override
//        public void readNBT (Capability<ISidedItemHandlerConfig> capability, ISidedItemHandlerConfig instance, EnumFacing side, NBTBase nbt) {
//        }
//    }

    static class CapabilityGuiContainer<T extends IGuiContainerProvider> implements Capability.IStorage<IGuiContainerProvider> {
        @Override
        public NBTBase writeNBT (Capability<IGuiContainerProvider> capability, IGuiContainerProvider instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT (Capability<IGuiContainerProvider> capability, IGuiContainerProvider instance, EnumFacing side, NBTBase nbt) {
        }
    }

    static class DefaultHudInfo implements IHudInfoProvider {
        @Override
        public List<HudInfoLine> getHUDLines() {
            return null;
        }
    }

    static class DefaultWrenchHandler implements ITeslaWrenchHandler {
        @Override
        public EnumActionResult onWrenchUse(TeslaWrench wrench, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            return EnumActionResult.PASS;
        }
    }

    static class DefaultGuiContainer implements IGuiContainerProvider {
        @Override
        public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
            return null;
        }

        @Override
        public List<Slot> getSlots(BasicTeslaContainer container) {
            return null;
        }

        @Override
        public BasicTeslaContainer getContainer(int id, EntityPlayer player) {
            return null;
        }

        @Override
        public BasicTeslaGuiContainer getGuiContainer(int id, EntityPlayer player) {
            return null;
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IHudInfoProvider.class, new TeslaCoreCapabilities.CapabilityHudInfoProvider<>(), DefaultHudInfo.class);
        CapabilityManager.INSTANCE.register(ITeslaWrenchHandler.class, new TeslaCoreCapabilities.CapabilityWrenchHandler<>(), DefaultWrenchHandler.class);
//        CapabilityManager.INSTANCE.register(ISidedItemHandlerConfig.class, new TeslaCoreCapabilities.CapabilitySidedConfig<>(), SidedItemHandlerConfig.class);
        CapabilityManager.INSTANCE.register(IGuiContainerProvider.class, new TeslaCoreCapabilities.CapabilityGuiContainer<>(), DefaultGuiContainer.class);
    }
}
