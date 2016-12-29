package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.ndrei.teslacorelib.inventory.EnergyStorage;

import java.util.List;

/**
 * Created by CF on 2016-12-25.
 */
public abstract class ElectricGenerator extends ElectricTileEntity {
    private EnergyStorage generatedPower = null;

    protected ElectricGenerator(int typeId) {
        super(typeId);
    }

    //region energy            methods

    @SuppressWarnings("WeakerAccess")
    protected long getMaxEnergy() {
        return 100000;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyInputRate() {
        return 0;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyOutputRate() {
        return 80;
    }

    protected long getEnergyFillRate() { return 160; }

    //endregion

    //#region work              methods

    protected abstract long consumeFuel();

    protected boolean isGeneratedPowerLostIfFull() {
        return true;
    }

    @Override
    public void protectedUpdate() {
        if ((this.generatedPower != null) && !this.generatedPower.isEmpty()) {
            long power = this.generatedPower.takePower(this.generatedPower.getOutputRate(), !this.isGeneratedPowerLostIfFull());
            long consumed = this.energyStorage.givePower(power);
            if ((consumed > 0) && this.isGeneratedPowerLostIfFull()) {
                this.generatedPower.takePower(consumed);
            }
        }

        if (((this.generatedPower == null) || this.generatedPower.isEmpty()) && !this.energyStorage.isFull() && !this.getWorld().isRemote) {
            this.generatedPower = null;

            long power = this.consumeFuel();
            if (power > 0) {
                this.generatedPower = new EnergyStorage(power, 0, this.getEnergyFillRate());
                this.generatedPower.givePower(power);
                this.forceSync();
            }
        }

        //#region distribute power

        // TODO: research if this should be done only on server side or not
        if (!this.energyStorage.isEmpty()) {
            List<EnumFacing> powerSides = this.sideConfig.getSidesForColor(this.energyStorage.getColor());
            if ((powerSides != null) && (powerSides.size() > 0)) {
                List<ITeslaConsumer> consumers = Lists.newArrayList();

                BlockPos pos = this.getPos();
                EnumFacing facing = this.getFacing();
                for (EnumFacing side : powerSides) {
                    EnumFacing oriented = this.orientFacing(side);
                    if ((oriented != EnumFacing.DOWN) && (oriented != EnumFacing.UP) && ((facing == EnumFacing.EAST) || (facing == EnumFacing.WEST))) {
                        oriented = oriented.getOpposite();
                    }

                    TileEntity entity = this.getWorld().getTileEntity(pos.offset(oriented));
                    if ((entity != null) && entity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, oriented.getOpposite())) {
                        ITeslaConsumer consumer = entity.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, oriented.getOpposite());
                        if (consumer != null) {
                            consumers.add(consumer);
                        }
                    }
                }

                if (consumers.size() > 0) {
                    long total = this.energyStorage.getOutputRate();
                    total = this.energyStorage.takePower(total, true);
                    long totalConsumed = 0;
                    int consumerCount = consumers.size();
                    for (ITeslaConsumer consumer : consumers) {
                        long perConsumer = total / consumerCount;
                        consumerCount--;
                        if (perConsumer > 0) {
                            long consumed = consumer.givePower(perConsumer, false);
                            if (consumed > 0) {
                                totalConsumed += consumed;
                            }
                        }
                    }

                    if (totalConsumed > 0) {
                        this.energyStorage.takePower(totalConsumed);
                    }
                }
            }
        }

        //#endregion
    }

    //#endregion
    //#region write/read/sync   methods

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.generatedPower != null) {
            compound.setTag("generated_energy", this.generatedPower.serializeNBT());
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("generated_energy")) {
            if (this.generatedPower == null) {
                this.generatedPower = new EnergyStorage(0, 0, 0);
            }
            this.generatedPower.deserializeNBT(compound.getCompoundTag("generated_energy"));
        }
    }

    //#endregion
}
