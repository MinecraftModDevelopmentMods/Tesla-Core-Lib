package net.ndrei.teslacorelib.inventory;

import mekanism.api.energy.IStrictEnergyAcceptor;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by CF on 2016-10-31.
 *
 * inspired (aka. mostly copy/pasted) from net.darkhax.tesla.api.implementation.BaseTeslaContainer
 * not using that directly because I don't want this mod lib to directly depend on 'tesla'
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "tesla"),
        @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaProducer", modid = "tesla"),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism")
})
public class EnergyStorage implements ITeslaConsumer, ITeslaHolder, ITeslaProducer, IStrictEnergyAcceptor, IEnergyStorage, INBTSerializable<NBTTagCompound>, ICapabilityProvider {
    private long stored = 0;
    private long capacity = 0;

    private long inputRate;
    private long outputRate;

    private EnumDyeColor color;

    private ISidedItemHandlerConfig sidedConfig = null;

    @SuppressWarnings("WeakerAccess")
    public EnergyStorage(EnumDyeColor color, long maxStoredEnergy, long inputRate, long outputRate) {
        this.color = color;
        this.capacity = maxStoredEnergy;
        this.inputRate = Math.max(0, inputRate);
        this.outputRate = Math.max(0, outputRate);
    }

    public EnumDyeColor getColor() {
        return this.color;
    }

    @SuppressWarnings("unused")
    public long workPerformed(long jobEnergy) {
        return this.workPerformed(jobEnergy, 1.0f);
    }

    @SuppressWarnings("WeakerAccess")
    public long workPerformed(long jobEnergy, float jobPercent) {
        long energy = Math.round((double)jobEnergy * Math.max(0, Math.min(1, jobPercent)));
        return this.takePower(energy);
    }

    @SuppressWarnings("unused")
    public long givePower(long energy) {
        return this.givePower(energy, false, true);
    }

    @SuppressWarnings("WeakerAccess")
    public long takePower(long energy) {
        return this.takePower(energy, false, true);
    }

    //region forge energy

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int)this.givePower(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int)this.takePower(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int)Math.min((long)Integer.MAX_VALUE, this.getStoredPower());
    }

    @Override
    public int getMaxEnergyStored() {
        return (int)Math.min((long)Integer.MAX_VALUE, this.getCapacity());
    }

    @Override
    public boolean canExtract() {
        return (this.getOutputRate() > 0);
    }

    @Override
    public boolean canReceive() {
        return (this.getInputRate() > 0);
    }

    //endregion
    //region IStrictEnergyAcceptor

    @Override
    @Optional.Method(modid = "Mekanism")
    public double getEnergy() {
        return this.getEnergyStored();
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public void setEnergy(double energy) {
        // TODO: ??
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public double getMaxEnergy() {
        return this.getMaxEnergyStored();
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public double transferEnergyToAcceptor(EnumFacing side, double amount) {
        int tesla = Math.round((float) amount * .4f);
        tesla = this.receiveEnergy(tesla, false);
        return tesla / .4;
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public boolean canReceiveEnergy(EnumFacing side) {
        return this.canReceive();
    }

    //endregion

    @Override
    public long getStoredPower() {
        return this.stored;
    }

    @Override
    public long givePower(long tesla, boolean simulated) {
        return this.givePower(tesla, simulated, false);
    }

    private long givePower(long tesla, boolean simulated, boolean forced) {
        final long acceptedTesla = forced
                ? Math.min(this.getCapacity() - this.stored, tesla)
                : Math.min(this.getCapacity() - this.stored, Math.min(this.getInputRate(), tesla));

        if (!simulated) {
            this.stored += acceptedTesla;
            this.onChanged();
        }

        return acceptedTesla;
    }

    @Override
    public long takePower(long tesla, boolean simulated) {
        return this.takePower(tesla, simulated, false);
    }

    private long takePower(long tesla, boolean simulated, boolean forced) {
        final long removedPower = forced
                ? Math.min(this.stored, tesla)
                : Math.min(this.stored, Math.min(this.getOutputRate(), tesla));

        if (!simulated) {
            this.stored -= removedPower;
            this.onChanged();
        }

        return removedPower;
    }

    @Override
    public long getCapacity() {
        return this.capacity;
    }

    //region util method

    /**
     * Sets the capacity of the the container. If the existing stored power is more than the
     * new capacity, the stored power will be decreased to match the new capacity.
     *
     * @param capacity The new capacity for the container.
     * @return The instance of the container being updated.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public EnergyStorage setCapacity(long capacity) {
        this.capacity = capacity;

        if (this.stored > capacity)
            this.stored = capacity;

        this.onChanged();
        return this;
    }

    /**
     * Gets the maximum amount of Tesla power that can be accepted by the container.
     *
     * @return The amount of Tesla power that can be accepted at any time.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public long getInputRate() {
        return this.inputRate;
    }

    /**
     * Sets the maximum amount of Tesla power that can be accepted by the container.
     *
     * @param rate The amount of Tesla power to accept at a time.
     * @return The instance of the container being updated.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public EnergyStorage setInputRate(long rate) {
        this.inputRate = rate;
        return this;
    }

    /**
     * Gets the maximum amount of Tesla power that can be pulled from the container.
     *
     * @return The amount of Tesla power that can be extracted at any time.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public long getOutputRate() {
        return this.outputRate;
    }

    /**
     * Sets the maximum amount of Tesla power that can be pulled from the container.
     *
     * @param rate The amount of Tesla power that can be extracted.
     * @return The instance of the container being updated.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public EnergyStorage setOutputRate(long rate) {
        this.outputRate = rate;
        return this;
    }

    /**
     * Sets both the input and output rates of the container at the same time. Both rates will
     * be the same.
     *
     * @param rate The input/output rate for the Tesla container.
     * @return The instance of the container being updated.
     */
    @SuppressWarnings("unused, WeakerAccess")
    public EnergyStorage setTransferRate(long rate) {
        this.setInputRate(rate);
        this.setOutputRate(rate);
        return this;
    }

    //endregion

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound dataTag = new NBTTagCompound();
        dataTag.setLong("TeslaPower", this.stored);
//        dataTag.setLong("TeslaCapacity", this.capacity);
//        dataTag.setLong("TeslaInput", this.inputRate);
//        dataTag.setLong("TeslaOutput", this.outputRate);

        return dataTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        long originalStored = this.stored;
        this.stored = nbt.getLong("TeslaPower");

//        if (nbt.hasKey("TeslaCapacity"))
//            this.capacity = nbt.getLong("TeslaCapacity");
//
//        if (nbt.hasKey("TeslaInput"))
//            this.inputRate = nbt.getLong("TeslaInput");
//
//        if (nbt.hasKey("TeslaOutput"))
//            this.outputRate = nbt.getLong("TeslaOutput");

        if (this.stored > this.getCapacity())
            this.stored = this.getCapacity();

        if (this.stored != originalStored) {
            this.onChanged();
        }
    }

    public void onChanged() {
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if ((this.sidedConfig != null) && (this.color != null) && this.sidedConfig.isSideSet(this.color, facing)) {
            if (capability == CapabilityEnergy.ENERGY) {
                return true;
            } else if (Loader.isModLoaded("tesla") && this.hasTeslaCapability(capability)) {
                return true;
            } else if (Loader.isModLoaded("Mekanism") && Objects.equals(capability.getName(), "mekanism.api.energy.IStrictEnergyAcceptor")) {
                return true; // TODO: not sure if this is the best way :S
            }
        }

        return false;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if ((this.sidedConfig != null) && (this.color != null) && this.sidedConfig.isSideSet(this.color, facing)) {
            if (capability == CapabilityEnergy.ENERGY) {
                return (T) this;
            } else if (Loader.isModLoaded("tesla") && this.hasTeslaCapability(capability)) {
                return (T) this;
            } else if (Loader.isModLoaded("Mekanism") && Objects.equals(capability.getName(), "mekanism.api.energy.IStrictEnergyAcceptor")) {
                return (T) this;
            }
        }

        return null;
    }

    @Optional.Method(modid = "tesla")
    private boolean hasTeslaCapability(Capability<?> capability) {
        if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
            return true;
        } else if ((this.getInputRate() > 0) && (capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
            return true;
        } else if ((this.getOutputRate() > 0) && (capability == TeslaCapabilities.CAPABILITY_PRODUCER)) {
            return true;
        }

        return false;
    }

    public void setSidedConfig(ISidedItemHandlerConfig sidedConfig, BoundingRectangle highlight) {
        if (this.sidedConfig == sidedConfig) {
            return;
        }

        this.sidedConfig = sidedConfig;
        if (this.sidedConfig != null) {
            this.sidedConfig.addColoredInfo("Energy", this.getColor(), highlight);
        }
    }
}