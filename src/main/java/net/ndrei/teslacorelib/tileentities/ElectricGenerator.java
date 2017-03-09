package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicRenderedGuiPiece;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.inventory.EnergyStorage;

import java.util.List;

/**
 * Created by CF on 2016-12-25.
 */
@SuppressWarnings("WeakerAccess")
public abstract class ElectricGenerator extends ElectricTileEntity {
    private EnergyStorage generatedPower = null;
    private ItemStackHandler chargePadItems;

    protected ElectricGenerator(int typeId) {
        super(typeId);
    }

    protected void initializeInventories() {
        super.initializeInventories();

        this.chargePadItems = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                ElectricGenerator.this.markDirty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        super.addInventory(new ColoredItemHandler(this.chargePadItems, EnumDyeColor.BROWN, "Charge Pad", new BoundingRectangle(34, 34, 18, 36)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return (!ItemStackUtil.isEmpty(stack) && stack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null));
            }

            @Override
            public boolean canExtractItem(int slot) {
                ItemStack stack = this.getStackInSlot(slot);
                if (!ItemStackUtil.isEmpty(stack)) {
                    ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
                    if (holder != null) {
                        return (holder.getCapacity() == holder.getStoredPower());
                    } else {
                        ITeslaConsumer consumer = stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
                        if (consumer != null) {
                            long consumed = consumer.givePower(1, true);
                            return (consumed == 0);
                        }
                    }
                }
                return true;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 0, 35, 35));
                slots.add(new FilteredSlot(this.getItemHandlerForContainer(), 1, 35, 53));

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                pieces.add(new BasicRenderedGuiPiece(25, 26, 27, 52,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 206, 4));

                return pieces;
            }
        });
        super.addInventoryToStorage(this.chargePadItems, "inv_charge_pad");
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
    protected void fuelConsumed() {}

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

            // TeslaCoreLib.logger.info("generated power: " + this.generatedPower.getStoredPower() + " / " + this.generatedPower.getCapacity());
            if (this.generatedPower.isEmpty()) {
                this.fuelConsumed();
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

    @Override
    protected void processImmediateInventories() {
        super.processImmediateInventories();

        for(int index = 0; index < 2; index++) {
            ItemStack stack = this.chargePadItems.getStackInSlot(index);
            if (ItemStackUtil.isEmpty(stack)) {
                continue;
            }

            long available = this.energyStorage.takePower(this.energyStorage.getOutputRate(), true);
            if (available == 0) {
                break;
            }

            ITeslaConsumer consumer = stack.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
            if (consumer != null) {
                long consumed = consumer.givePower(available, false);
                if (consumed > 0) {
                    this.energyStorage.takePower(consumed, false);
                }
            }
        }
    }

    //#endregion
    //#region write/read/sync   methods

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.generatedPower != null) {
            compound.setTag("generated_energy", this.generatedPower.serializeNBT());
        }

//        if (this.chargePadItems != null) {
//            NBTTagCompound nbt = this.chargePadItems.serializeNBT();
//            if (nbt != null) {
//                compound.setTag("inv_charge_pad", nbt);
//            }
//        }

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

//        if (compound.hasKey("inv_charge_pad", Constants.NBT.TAG_COMPOUND) && (this.chargePadItems != null)) {
//            this.chargePadItems.deserializeNBT(compound.getCompoundTag("inv_charge_pad"));
//        }
    }

    //#endregion

    @SuppressWarnings("unused")
    public long getGeneratedPowerCapacity() {
        return (this.generatedPower == null) ? 0 : this.generatedPower.getCapacity();
    }

    @SuppressWarnings("unused")
    public long getGeneratedPowerStored() {
        return (this.generatedPower == null) ? 0 : this.generatedPower.getStoredPower();
    }

    @SuppressWarnings("unused")
    public long getGeneratedPowerReleaseRate() {
        return (this.generatedPower == null) ? 0 : this.generatedPower.getOutputRate();
    }
}
