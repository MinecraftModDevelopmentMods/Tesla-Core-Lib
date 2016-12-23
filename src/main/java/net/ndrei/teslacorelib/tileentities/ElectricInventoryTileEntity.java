package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredContainedItemInventory;
import net.ndrei.teslacorelib.inventory.SidedItemHandler;
import net.ndrei.teslacorelib.netsync.SimpleNBTMessage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2016-12-18.
 */
public abstract class ElectricInventoryTileEntity extends ElectricTileEntity {
    private SidedItemHandler inventory;
    protected ItemStackHandler energyItems;

    protected ElectricInventoryTileEntity(int typeId) {
        super(typeId);

        this.inventory = new SidedItemHandler(super.sideConfig);

        this.energyItems = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                ElectricInventoryTileEntity.this.markDirty();
            }
        };
        this.inventory.addItemHandler(new ColoredContainedItemInventory(EnumDyeColor.CYAN, "Energy Items", this.energyItems) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return ElectricInventoryTileEntity.this.canInsertEnergyItem(slot, stack);
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
        this.sideConfig.addColoredInfo("Energy Items", EnumDyeColor.CYAN, new BoundingRectangle(25, 25, 18, 54));
    }

    private boolean canInsertEnergyItem(int slot, ItemStack stack) {
        if (slot != 0) {
            return false;
        }

        // TODO: test for energy cells
        return true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        NBTTagCompound nbt = this.energyItems.serializeNBT();
        if (nbt != null) {
            compound.setTag("inv_energy_items", nbt);
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("inv_energy_items", Constants.NBT.TAG_COMPOUND)) {
            this.energyItems.deserializeNBT(compound.getCompoundTag("inv_energy_items"));
        }
    }

    @Override
    protected SimpleNBTMessage processClientMessage(String messageType, NBTTagCompound compound) {
        if ((messageType != null) && messageType.equals("TOGGLE_SIDE")) {
            EnumDyeColor color = EnumDyeColor.byMetadata(compound.getInteger("color"));
            EnumFacing facing = EnumFacing.getFront(compound.getInteger("side"));
            TeslaCoreLib.logger.info("Processing message " + messageType + " on server: " + color + " " + facing);
            this.sideConfig.toggleSide(color, facing);
            this.markDirty();
        }
        return null;
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = Lists.newArrayList();
        pieces.add(new TeslaEnergyLevelPiece(7, 25, this.energyStorage));
        pieces.add(new PlayerInventoryBackground(7, 101, 162, 54));
        SideConfigurator configurator = new SideConfigurator(7, 101, 162, 54, this.sideConfig, this);
        pieces.add(configurator);
        pieces.add(new SideConfigSelector(7, 81, 162, 18, this.sideConfig, configurator));

        for(int i = 0; i < this.inventory.getInventories(); i++) {
            IItemHandler handler = this.inventory.getInventory(i);
            if(handler instanceof IGuiContainerPiecesProvider) {
                List<IGuiContainerPiece> childPieces = ((IGuiContainerPiecesProvider)handler).getGuiContainerPieces(container);
                if ((childPieces != null) && (childPieces.size() > 0)) {
                    pieces.addAll(childPieces);
                }
            }
        }

        return pieces;
    }

    @Override
    public List<Slot> getSlots(BasicTeslaContainer container) {
        List<Slot> slots = Lists.newArrayList();

        for(int i = 0; i < this.inventory.getInventories(); i++) {
            IItemHandler handler = this.inventory.getInventory(i);
            if(handler instanceof IContainerSlotsProvider) {
                List<Slot> childSlots = ((IContainerSlotsProvider)handler).getSlots(container);
                if ((childSlots != null) && (childSlots.size() > 0)) {
                    slots.addAll(childSlots);
                }
            }
        }

        return slots;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            EnumFacing oriented = this.orientFacing(facing);
            int[] slots = this.inventory.getSlotsForFace(oriented);
            return ((slots != null) && (slots.length > 0));
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            EnumFacing oriented = this.orientFacing(facing);
            return (T)this.inventory.getSideWrapper(oriented);
        }
        return super.getCapability(capability, facing);
    }
}
