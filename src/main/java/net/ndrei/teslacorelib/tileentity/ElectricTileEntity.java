package net.ndrei.teslacorelib.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.block.OrientedBlock;
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.capabilities.hud.IHudInfoProvider;
import net.ndrei.teslacorelib.netsync.ISimpleNBTMessageHandler;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
public abstract class ElectricTileEntity extends TileEntity implements ITickable, IWorkProgressProvider, IHudInfoProvider, ISimpleNBTMessageHandler {
    private static final int SYNC_ON_TICK = 20;
    private int syncTick = SYNC_ON_TICK;

    private int lastWorkTicks = 0;
    private int workTick = 0;

    @SuppressWarnings("WeakerAccess")
    protected EnergyStorage energyStorage;

    private int typeId; // used for message sync

    @SuppressWarnings("WeakerAccess")
    protected boolean outOfPower = false;

    @SuppressWarnings("unused")
    protected ElectricTileEntity(int typeId) {
        this.typeId = typeId;

        this.energyStorage = new EnergyStorage(this.getMaxEnergy(), this.getEnergyInputRate(), this.getEnergyOutputRate()) {
            @Override
            public void onChanged() {
                ElectricTileEntity.this.markDirty();
                ElectricTileEntity.this.forceSync();
            }
        };
    }

    protected EnumFacing getFacing() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        if (state.getBlock() instanceof OrientedBlock) {
            return state.getValue(OrientedBlock.FACING);
        }
        return EnumFacing.NORTH;
    }

    //region energy            methods

    @SuppressWarnings("WeakerAccess")
    protected long getMaxEnergy() {
        return 50000;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyInputRate() {
        return 80;
    }

    @SuppressWarnings("WeakerAccess")
    protected long getEnergyOutputRate() {
        return 0;
    }

    @SuppressWarnings({"WeakerAccess", "ConstantConditions"})
    protected void forceSync() {
        if ((this.getWorld() != null) && !this.getWorld().isRemote) {
            this.syncTick = SYNC_ON_TICK;
        }
    }

    //endregion
    //region work              methods

    @SuppressWarnings("WeakerAccess")
    protected int getWorkTicks() {
        return 40;
    }

    @SuppressWarnings("WeakerAccess")
    protected int getEnergyForWork() {
        return 800;
    }

    @Override
    public int getJobTicks() {
        return this.lastWorkTicks;
    }

    @Override
    public float getJobProgress() {
        if (this.getJobTicks() <= 0) {
            return 0;
        }
        return (float) Math.min(this.getJobTicks(), Math.max(0, this.workTick)) / (float) this.getJobTicks();
    }

    @Override
    public boolean hasJob() {
        return (this.lastWorkTicks >= 0);
    }

    //endregion

    //region storage & sync    methods

    @SuppressWarnings("WeakerAccess")
    protected int getEntityTypeId() {
        return this.typeId;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("energy")) {
            this.energyStorage.deserializeNBT(compound.getCompoundTag("energy"));
        }

        this.lastWorkTicks = compound.getInteger("tick_lastWork");
        this.workTick = compound.getInteger("tick_work");
        this.syncTick = compound.getInteger("tick_sync");
        this.outOfPower = compound.getBoolean("out_of_power");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setTag("energy", this.energyStorage.serializeNBT());

        compound.setInteger("tick_work", this.workTick);
        compound.setInteger("tick_lastWork", this.lastWorkTicks);
        compound.setInteger("tick_sync", this.syncTick);
        compound.setBoolean("out_of_power", this.outOfPower);
        return compound;
    }

    private NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("__tetId", this.getEntityTypeId());
        return this.writeToNBT(compound);
    }

    @Override
    public SimpleNBTMessage handleMessage(SimpleNBTMessage message) {
        if (this.getWorld().isRemote) {
            NBTTagCompound compound = (message == null) ? null : message.getCompound();
            if (compound != null) {
                int tetId = compound.getInteger("__tetId");
                if (tetId == this.getEntityTypeId()) {
                    this.processServerMessage(compound);
                }
            }
        }
        return null;
    }

    protected void processServerMessage(NBTTagCompound compound) {
        this.readFromNBT(compound);
    }

    //endregion

    //region capability & info methods

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
//        EnumFacing machineFacing = this.getFacing();
//        Boolean isFront = (machineFacing != null) && (machineFacing == facing);

        if ((this.energyStorage != null) && this.energyStorage.hasCapability(capability, facing)) {
            return true;
        } else if (capability == TeslaCoreCapabilities.CAPABILITY_HUD_INFO) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
//        EnumFacing machineFacing = this.getFacing();
//        Boolean isFront = (machineFacing != null) && (machineFacing == facing);

        if (capability == TeslaCoreCapabilities.CAPABILITY_HUD_INFO) {
            return (T) this;
        } else if (this.energyStorage != null) {
            T c = this.energyStorage.getCapability(capability, facing);
            if (c != null) {
                return c;
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public List<HudInfoLine> getHUDLines() {
        List<HudInfoLine> list = Lists.newArrayList();

        if (this.outOfPower) {
            list.add(new HudInfoLine(Color.RED,
                    new Color(255, 0, 0, 42),
                    "out of power")
                    .setTextAlignment(HudInfoLine.TextAlignment.CENTER));
        }

        return list;
    }

    //endregion

    @Override
    public void update() {
        if (this.outOfPower) {
            int energy = this.getEnergyForWork();
            if (this.energyStorage.getEnergyStored() >= energy) {
                this.outOfPower = false;
                this.forceSync();
            }
        }

        if (!this.outOfPower) {
            this.workTick++;

            if (this.workTick > this.lastWorkTicks) {
                this.lastWorkTicks = this.getWorkTicks();
                this.workTick = 0;

                int energy = this.getEnergyForWork();
                if (this.energyStorage.getEnergyStored() >= energy) {
                    if (!this.getWorld().isRemote) {
                        float work = this.performWork();
                        if (work > 0) {
                            this.energyStorage.workPerformed(energy, work);
                        }
                    } else {
                        this.outOfPower = true;
                    }
                    this.forceSync();
                }
            }
        }

        if (!this.getWorld().isRemote) {
            this.syncTick++;
            if (this.syncTick >= SYNC_ON_TICK) {
                TeslaCoreLib.network.send(new SimpleNBTMessage(this, this.writeToNBT()));
                this.syncTick = 0;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected abstract float performWork();
}
