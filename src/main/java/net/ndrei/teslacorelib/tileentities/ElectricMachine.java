package net.ndrei.teslacorelib.tileentities;

import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.capabilities.hud.HudInfoLine;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.WorkEnergyIndicatorPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.EnergyStorage;

import java.awt.*;
import java.util.List;

/**
 * Created by CF on 2016-12-18.
 */
public abstract class ElectricMachine extends ElectricTileEntity implements IWorkEnergyProvider {
    private int lastWorkTicks = 0;
    private int workTick = 0;

    @SuppressWarnings("WeakerAccess")
    protected boolean outOfPower = false;

    private ItemStackHandler energyItems;

    private EnergyStorage workEnergy;

    protected ElectricMachine(int typeId) {
        super(typeId);
    }

    //#region inventories       methods

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.energyItems = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                ElectricMachine.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.energyItems, EnumDyeColor.CYAN, "Energy Items", new BoundingRectangle(25, 25, 18, 54)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ElectricMachine.this.canInsertEnergyItem(slot, stack);
            }

            @Override
            public boolean canExtractItem(int slot) {
                return (slot != 0);
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, 26, 26));
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 1, 26, 62));

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                pieces.add(new BasicRenderedGuiPiece(25, 25, 18, 54,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 78, 189));

                return pieces;
            }
        });
    }

    private boolean canInsertEnergyItem(int slot, ItemStack stack) {
        if (slot != 0) {
            return false;
        }

        ITeslaProducer tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
        if (tesla != null) {
            ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
            if ((holder == null) || (holder.getStoredPower() > 0)) {
                return true;
            }
        }
        return false;
    }

    //#endregion
    //#region write/read/sync   methods

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setInteger("tick_work", this.workTick);
        compound.setInteger("tick_lastWork", this.lastWorkTicks);
        compound.setBoolean("out_of_power", this.outOfPower);

        if (this.workEnergy != null) {
            compound.setTag("work_energy", this.workEnergy.serializeNBT());
        }

        if (this.energyItems != null) {
            NBTTagCompound nbt = this.energyItems.serializeNBT();
            if (nbt != null) {
                compound.setTag("inv_energy_items", nbt);
            }
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.lastWorkTicks = compound.getInteger("tick_lastWork");
        this.workTick = compound.getInteger("tick_work");
        this.outOfPower = compound.getBoolean("out_of_power");

        if (compound.hasKey("work_energy")) {
            if (this.workEnergy == null) {
                this.workEnergy = new EnergyStorage(this.getEnergyForWork(), this.getEnergyForWorkRate(), 0);
            }
            this.workEnergy.deserializeNBT(compound.getCompoundTag("work_energy"));
        }

        if (compound.hasKey("inv_energy_items", Constants.NBT.TAG_COMPOUND)) {
            this.energyItems.deserializeNBT(compound.getCompoundTag("inv_energy_items"));
        }
    }

    //#endregion
    //#region gui / container   methods

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        pieces.add(new WorkEnergyIndicatorPiece(this, 7, 20));

        return pieces;
    }

//    @Override
//    public List<Slot> getSlots(BasicTeslaContainer container) {
//        List<Slot> slots = super.getSlots(container);
//
//
//        return slots;
//    }

    @Override
    public List<HudInfoLine> getHUDLines() {
        List<HudInfoLine> list = super.getHUDLines();

        if (this.outOfPower) {
            list.add(new HudInfoLine(Color.RED,
                    new Color(255, 0, 0, 42),
                    "out of power")
                    .setTextAlignment(HudInfoLine.TextAlignment.CENTER));
        }

        return list;
    }

    //#endregion
    //#region capabilities      methods

//    @Override
//    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
//        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            EnumFacing oriented = this.orientFacing(facing);
//            int[] slots = this.inventory.getSlotsForFace(oriented);
//            return ((slots != null) && (slots.length > 0));
//        }
//        return super.hasCapability(capability, facing);
//    }

//    @Override
//    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
//        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            EnumFacing oriented = this.orientFacing(facing);
//            return (T)this.inventory.getSideWrapper(oriented);
//        }
//        return super.getCapability(capability, facing);
//    }

    //#endregion

    //region work              methods

    protected int getWorkTicks() {
        return 40;
    }

    protected int getEnergyForWork() {
        return 600;
    }

    protected int getEnergyForWorkRate() {
        return 20;
    }

    @Override
    public long getWorkEnergyCapacity() {
        return (this.workEnergy != null) ? this.workEnergy.getCapacity() : 0;
    }

    @Override
    public long getWorkEnergyStored() {
        return (this.workEnergy != null) ? this.workEnergy.getEnergyStored() : 0;
    }

    @Override
    public long getWorkEnergyTick() {
        return (this.workEnergy != null) ? this.workEnergy.getInputRate() : 0;
    }

    @Override
    public void protectedUpdate() {
        if (this.workEnergy == null) {
            this.workEnergy = new EnergyStorage(this.getEnergyForWork(), this.getEnergyForWorkRate(), 0);
        }

        if (!this.workEnergy.isFull()) {
            long toTransfer = Math.min(
                    this.workEnergy.getCapacity() - this.workEnergy.getStoredPower(),
                    this.workEnergy.getInputRate());
            long transfered = this.energyStorage.takePower(toTransfer);
            this.workEnergy.givePower(transfered);
        }

        if (this.workEnergy.isFull() && !this.getWorld().isRemote) {
            float work = this.performWork();
            long oldCapacity = this.workEnergy.getCapacity();
            this.workEnergy = new EnergyStorage(this.getEnergyForWork(), this.getEnergyForWorkRate(), 0);
            this.workEnergy.givePower(Math.round(oldCapacity * Math.max(0, Math.min(1, work))));
            this.forceSync();
        }

//        if (this.outOfPower) {
//            int energy = this.getEnergyForWork();
//            if (this.energyStorage.getEnergyStored() >= energy) {
//                this.outOfPower = false;
//                this.forceSync();
//            }
//        }

//        if (!this.outOfPower) {
//            this.workTick++;
//
//            if (this.workTick > this.lastWorkTicks) {
//                this.lastWorkTicks = this.getWorkTicks();
//                this.workTick = 0;
//
//                int energy = this.getEnergyForWork();
//                if (this.energyStorage.getEnergyStored() >= energy) {
//                    if (!this.getWorld().isRemote) {
//                        float work = this.performWork();
//                        if (work > 0) {
//                            this.energyStorage.workPerformed(energy, work);
//                        }
//                    } else {
//                        this.outOfPower = true;
//                    }
//                    this.forceSync();
//                }
//            }
//        }
    }

    @SuppressWarnings("WeakerAccess")
    protected abstract float performWork();


    @Override
    protected void processImmediateInventories() {
        super.processImmediateInventories();

        if (this.energyItems != null) {
            ItemStack stack = this.energyItems.getStackInSlot(0);
            if (!stack.isEmpty()) {
                ITeslaProducer producer = stack.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null);
                if (producer != null) {
                    long power = producer.takePower(this.energyStorage.getInputRate(), true);
                    if (power == 0) {
                        this.discardUsedEnergyItem();
                    } else {
                        long accepted = this.energyStorage.givePower(power, false);
                        producer.takePower(accepted, false);
                    }
                } else {
                    this.discardUsedEnergyItem();
                }
            }
        }
    }

    private void discardUsedEnergyItem() {
        ItemStack stack = this.energyItems.getStackInSlot(0);
        ItemStack remaining = this.energyItems.insertItem(1, stack, false);
        this.energyItems.setStackInSlot(0, remaining);
    }

    //endregion
}
