package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig;
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer;
import net.ndrei.teslacorelib.gui.FluidTankPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiece;
import net.ndrei.teslacorelib.gui.IGuiContainerPiecesProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CF on 2016-12-29.
 */
public class SidedFluidHandler extends FluidStorage implements ICapabilityProvider, IGuiContainerPiecesProvider {
    private ISidedItemHandlerConfig sidedConfig;

    public SidedFluidHandler(ISidedItemHandlerConfig sidedConfig) {
        this.sidedConfig = sidedConfig;
    }

    private List<IFluidTank> getTanksForSide(EnumFacing facing) {
        List<IFluidTank> list = Lists.newArrayList();

        for(IFluidTank tank: this.getTanks()) {
            if (tank instanceof ColoredFluidHandler) {
                EnumDyeColor color = ((ColoredFluidHandler)tank).getColor();
                if ((color != null) && ((facing == null) || this.sidedConfig.isSideSet(color, facing))) {
                    list.add(tank);
                }
            }
        }

        return list;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            List<IFluidTank> tanks = this.getTanksForSide(facing);
            return (tanks.size() > 0);
        }

        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            List<IFluidTank> tanks = this.getTanksForSide(facing);
            FluidStorage tempStorage = new FluidStorage();

            for(IFluidTank tank: tanks) {
                tempStorage.addTank(tank);
            }

            return (T)tempStorage;
        }

        return null;
    }

    @Override
    public List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container) {
        List<IGuiContainerPiece> list = Lists.newArrayList();

        for(IFluidTank tank : this.getTanks()) {
            if (tank instanceof ColoredFluidHandler) {
                BoundingRectangle box = ((ColoredFluidHandler) tank).getBoundingBox();
                if (box != null) {
                    list.add(new FluidTankPiece(tank, box.getLeft(), box.getTop()));
                }
            }
        }

        return list;
    }
}
