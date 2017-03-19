package net.ndrei.teslacorelib.tileentities;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.TeslaEnergyLevelPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.EnergyStorage;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings("WeakerAccess")
public abstract class ElectricTileEntity extends SidedTileEntity {
    protected EnergyStorage energyStorage;

    @SuppressWarnings("unused")
    protected ElectricTileEntity(int typeId) {
        super(typeId);

    }

    private void notifyNeighbours() {
        if (this.getWorld() != null) {
            this.getWorld().notifyNeighborsOfStateChange(
                    this.getPos(),
                    this.getBlockType()
            );
        }
    }

    //region inventory         methods

    @Override
    protected void initializeInventories() {
        this.energyStorage = new EnergyStorage(this.getMaxEnergy(), this.getEnergyInputRate(), this.getEnergyOutputRate()) {
            @Override
            public void onChanged() {
                ElectricTileEntity.this.markDirty();
                ElectricTileEntity.this.forceSync();
            }
        };
        this.energyStorage.setSidedConfig(EnumDyeColor.LIGHT_BLUE, this.sideConfig, this.getEnergyBoundingBox());

        super.initializeInventories();
    }

    protected BoundingRectangle getEnergyBoundingBox() {
        return new BoundingRectangle(7, 25, 18, 54);
    }

    //endregion

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

    //endregion

    //region storage & sync    methods

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("energy")) {
            this.energyStorage.deserializeNBT(compound.getCompoundTag("energy"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setTag("energy", this.energyStorage.serializeNBT());
        return compound;
    }

    //endregion

    //region capability & info methods

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        EnumFacing oriented = this.orientFacing(facing);
        
        if ((this.energyStorage != null) && this.energyStorage.hasCapability(capability, oriented)) {
            return true;
        }

        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        EnumFacing oriented = this.orientFacing(facing);

        if (this.energyStorage != null) {
            T c = this.energyStorage.getCapability(capability, oriented);
            if (c != null) {
                return c;
            }
        }

        return super.getCapability(capability, facing);
    }

    //endregion

    //#region gui / containers  nethods

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> pieces = super.getGuiContainerPieces(container);

        BoundingRectangle energyBox = this.getEnergyBoundingBox();
        if (energyBox != null) {
            pieces.add(new TeslaEnergyLevelPiece(energyBox.getLeft(), energyBox.getTop(), this.energyStorage));
        }

        return pieces;
    }

    //#endregion

    @Override
    public final void innerUpdate() {
        this.protectedUpdate();
        this.energyStorage.processStatistics();
    }

    protected abstract void protectedUpdate();
}
