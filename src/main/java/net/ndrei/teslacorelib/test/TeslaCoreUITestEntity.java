package net.ndrei.teslacorelib.test;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.ndrei.teslacorelib.containers.BasicTeslaContainer;
import net.ndrei.teslacorelib.containers.FilteredSlot;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.FluidTankPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TiledRenderedGuiPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandler;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;

import java.util.List;

/**
 * Created by CF on 2016-12-21.
 */
public final class TeslaCoreUITestEntity extends ElectricMachine {
    private IFluidTank waterTank;
    private IFluidTank lavaTank;

    private ItemStackHandler inputs;
    private ItemStackHandler outputs;

    public TeslaCoreUITestEntity() {
        super(-1);
    }

    //#region inventories       methods

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                new BoundingRectangle(43, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                new BoundingRectangle(43+18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));
        super.ensureFluidItems();

        this.inputs = new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                TeslaCoreUITestEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", new BoundingRectangle(115, 25, 54, 18)) {
            @Override
            public boolean canExtractItem(int slot) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                for(int x = 0; x < 3; x++) {
                    slots.add(new FilteredSlot(this.getItemHandlerForContainer(), x, box.getLeft() + 1 + x * 18, box.getTop() + 1));
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        3, 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN));

                return pieces;
            }
        });

        this.outputs = new ItemStackHandler(6) {
            @Override
            protected void onContentsChanged(int slot) {
                TeslaCoreUITestEntity.this.markDirty();
            }
        };
        super.addInventory(new ColoredItemHandler(this.outputs, EnumDyeColor.PURPLE, "Output Items", new BoundingRectangle(115, 43, 54, 36)) {
            @Override
            public boolean canInsertItem(int slot, ItemStack stack) {
                return false;
            }

            @Override
            public List<Slot> getSlots(BasicTeslaContainer container) {
                List<Slot> slots = super.getSlots(container);

                BoundingRectangle box = this.getBoundingBox();
                for(int x = 0; x < 3; x++) {
                    for(int y = 0; y < 2; y++) {
                        slots.add(new FilteredSlot(this.getItemHandlerForContainer(), y * 3 +  x,
                                box.getLeft() + 1 + x * 18, box.getTop() + 1 + y * 18));
                    }
                }

                return slots;
            }

            @Override
            public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
                List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

                BoundingRectangle box = this.getBoundingBox();
                pieces.add(new TiledRenderedGuiPiece(box.getLeft(), box.getTop(), 18, 18,
                        3, 2,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE));

                return pieces;
            }
        });
    }

    //#endregion
    //#region write/read/sync   methods

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        if (this.inputs != null) {
            NBTTagCompound nbt = this.inputs.serializeNBT();
            if (nbt != null) {
                compound.setTag("inv_inputs", nbt);
            }
        }

        if (this.outputs != null) {
            NBTTagCompound nbt = this.outputs.serializeNBT();
            if (nbt != null) {
                compound.setTag("inv_outputs", nbt);
            }
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("inv_inputs", Constants.NBT.TAG_COMPOUND) && (this.inputs != null)) {
            this.inputs.deserializeNBT(compound.getCompoundTag("inv_inputs"));
        }

        if (compound.hasKey("inv_outputs", Constants.NBT.TAG_COMPOUND) && (this.outputs != null)) {
            this.outputs.deserializeNBT(compound.getCompoundTag("inv_outputs"));
        }
    }

    //#endregion

    @Override
    protected float performWork() {
        float result = 0.0f;

        if ((this.waterTank != null) && (this.lavaTank != null) && (this.outputs != null)) {
            FluidStack water = this.waterTank.drain(250, false);
            FluidStack lava = this.lavaTank.drain(125, false);
            if ((water != null) && (water.amount == 250) && (lava != null) && (lava.amount == 125)) {
                ItemStack cobble = new ItemStack(Blocks.COBBLESTONE, 1);
                cobble = ItemHandlerHelper.insertItem(this.outputs, cobble, false);
                if (cobble.isEmpty()) {
                    this.waterTank.drain(250, true);
                    this.lavaTank.drain(125, true);
                    result = 0.25f;
                }
            }
        }

        if ((this.inputs != null) && (this.outputs != null)) {
            boolean moved = true;
            while (moved && (result <= .85f)) {
                moved = false;
                for(int x = 0; x < 3; x++) {
                    ItemStack stack = this.inputs.extractItem(x, 1, true);
                    if (stack.isEmpty()) {
                        continue;
                    }
                    stack = ItemHandlerHelper.insertItem(this.outputs, stack, false);
                    if (stack.isEmpty()) {
                        this.inputs.extractItem(x, 1, false);
                        result += .15f;
                        moved = true;
                        break;
                    }
                }
            }
        }

        return result;
    }
}
