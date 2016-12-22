package net.ndrei.teslacorelib.tileentities;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.containers.IContainerSlotsProvider;
import net.ndrei.teslacorelib.gui.*;
import net.ndrei.teslacorelib.inventory.ColoredContainedItemInventory;
import net.ndrei.teslacorelib.inventory.SidedItemHandler;

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

        this.energyItems = new ItemStackHandler(2);
        this.inventory.addItemHandler(new ColoredContainedItemInventory(EnumDyeColor.LIGHT_BLUE, "Energy Items", this.energyItems) {
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
            compound.setTag("inv_light_blue", nbt);
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("inv_light_blue", Constants.NBT.TAG_COMPOUND)) {
            this.energyItems.deserializeNBT(compound.getCompoundTag("inv_light_blue"));
        }
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = Lists.newArrayList();
        pieces.add(new TeslaEnergyLevelPiece(7, 25, this.energyStorage));
        pieces.add(new PlayerInventoryBackground(7, 101, 162, 54));
        pieces.add(new SideConfigurator(7, 81, 162, 18, this.sideConfig));

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
}
