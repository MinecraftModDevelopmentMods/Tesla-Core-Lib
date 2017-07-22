package net.ndrei.teslacorelib.test

import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.gui.FluidTankPiece
import net.ndrei.teslacorelib.inventory.*
import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-06-27.
 */
class TeslaCoreUITestEntity : ElectricMachine(-1) {
    private lateinit var waterTank: IFluidTank
    private lateinit var lavaTank: IFluidTank
    private lateinit var tempTank: IFluidTank

    private lateinit var inputs: LockableItemHandler
    private lateinit var outputs: ItemStackHandler

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        // auto filtered
        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.lavaTank = object: FluidTank(5000) {
            override fun onContentsChanged() {
                this@TeslaCoreUITestEntity.markDirty()
            }

            override fun canFillFluidType(fluid: FluidStack?)
                    = fluid?.fluid == FluidRegistry.LAVA
        }
        super.addFluidTank(this.lavaTank, EnumDyeColor.RED, "Lava Tank",
                BoundingRectangle(43 + 18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))

        this.tempTank = object: FluidTank(5000) {
            override fun onContentsChanged() {
                this@TeslaCoreUITestEntity.markDirty()
            }
        }
        super.addFluidTank(object: ColoredFluidHandler(this.tempTank, EnumDyeColor.LIME, "Temp Tank",
                BoundingRectangle(43 + 18 + 18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT)) {
            // override fun acceptsFluid(fluid: FluidStack) =  fluid.fluid == FluidRegistry.WATER
        }, null)

        super.ensureFluidItems()

        this.inputs = object : LockableItemHandler(3) {
            override fun onContentsChanged(slot: Int) {
                this@TeslaCoreUITestEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.inputs, EnumDyeColor.GREEN, "Input Items", BoundingRectangle(115, 25, 54, 18)) {
            override fun canExtractItem(slot: Int): Boolean {
                return false
            }
        })
        super.addInventoryToStorage(this.inputs, "inv_inputs")

        this.outputs = object : ItemStackHandler(6) {
            override fun onContentsChanged(slot: Int) {
                this@TeslaCoreUITestEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs!!, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(115, 43, 54, 36)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return false
            }
        })
        super.addInventoryToStorage(this.outputs, "inv_outputs")
    }

    //#endregion

    override fun performWork(): Float {
        var result = 0.0f

        if (this.waterTank != null && this.lavaTank != null) {
            val water = this.waterTank!!.drain(250, false)
            val lava = this.lavaTank!!.drain(125, false)
            if (water != null && water.amount == 250 && lava != null && lava.amount == 125) {
                var cobble = ItemStack(Blocks.COBBLESTONE, 1)
                cobble = ItemStackUtil.insertItems(this.outputs, cobble, false)
                if (ItemStackUtil.isEmpty(cobble)) {
                    this.waterTank!!.drain(250, true)
                    this.lavaTank!!.drain(125, true)
                    result = 0.25f
                }
            }
        }

        var moved = true
        while (moved && result <= .85f) {
            moved = false
            for (x in 0..2) {
                var stack = this.inputs!!.extractItem(x, 1, true)
                if (ItemStackUtil.isEmpty(stack)) {
                    continue
                }
                stack = ItemStackUtil.insertItems(this.outputs, stack, false)
                if (ItemStackUtil.isEmpty(stack)) {
                    this.inputs!!.extractItem(x, 1, false)
                    result += .15f
                    moved = true
                    break
                }
            }
        }

        return result
    }
}
