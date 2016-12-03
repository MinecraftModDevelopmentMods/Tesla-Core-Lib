package net.ndrei.teslacorelib.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.capabilities.hud.IHudInfoProvider;

import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
public class TeslaCoreCapabilities {
    @CapabilityInject(IHudInfoProvider.class)
    public static Capability<IHudInfoProvider> CAPABILITY_HUD_INFO = null;

    public static class CapabilityHudInfoConsumer<T extends IHudInfoProvider> implements Capability.IStorage<IHudInfoProvider> {
        @Override
        public NBTBase writeNBT (Capability<IHudInfoProvider> capability, IHudInfoProvider instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT (Capability<IHudInfoProvider> capability, IHudInfoProvider instance, EnumFacing side, NBTBase nbt) {
        }
    }

    public static class DefaultHudInfo implements IHudInfoProvider {
        @Override
        public List<HudInfoLine> getHUDLines() {
            return null;
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IHudInfoProvider.class, new TeslaCoreCapabilities.CapabilityHudInfoConsumer<>(), DefaultHudInfo.class);
    }
}
