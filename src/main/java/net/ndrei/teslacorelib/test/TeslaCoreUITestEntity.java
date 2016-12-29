package net.ndrei.teslacorelib.test;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.gui.FluidTankPiece;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.tileentities.ElectricMachine;

/**
 * Created by CF on 2016-12-21.
 */
public final class TeslaCoreUITestEntity extends ElectricMachine {
    private IFluidTank waterTank;
    private IFluidTank lavaTank;

    public TeslaCoreUITestEntity() {
        super(-1);
    }

    @Override
    protected void initializeInventories() {
        super.initializeInventories();

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                new BoundingRectangle(43, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                new BoundingRectangle(43+18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT));
    }

    @Override
    protected float performWork() {
        if (this.waterTank != null) {
            this.waterTank.drain(250, true);
        }
        if (this.lavaTank != null) {
            this.lavaTank.drain(125, true);
        }

        return 0.25f;
    }
}
