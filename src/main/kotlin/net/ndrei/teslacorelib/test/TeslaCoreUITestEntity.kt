package net.ndrei.teslacorelib.test

import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.IItemHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.FluidTankType
import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-06-27.
 */
class TeslaCoreUITestEntity : ElectricMachine(-1) {
    private lateinit var waterTank: IFluidTank
    private lateinit var lavaTank: IFluidTank
    private lateinit var tempTank: IFluidTank

    private lateinit var inputs: IItemHandler
    private lateinit var outputs: IItemHandler

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.waterTank = this.addSimpleFluidTank(5000, "Water Tank", EnumDyeColor.BLUE, 43, 25,
            FluidTankType.INPUT, { it.fluid == FluidRegistry.WATER })
        this.lavaTank = this.addSimpleFluidTank(5000, "Lava Tank", EnumDyeColor.RED, 43 + 18, 25,
            FluidTankType.INPUT, { it.fluid == FluidRegistry.LAVA })
        this.tempTank = this.addSimpleFluidTank(5000, "Temp Tank", EnumDyeColor.LIME, 43 + 18 + 18, 25)
        super.ensureFluidItems()

        this.inputs = this.addSimpleInventory(3, "inv_inputs", EnumDyeColor.GREEN, "Input Items",
            BoundingRectangle.slots(115, 25, 3, 1),
            outputFilter = { _, _ -> false})
        this.outputs = this.addSimpleInventory(6, "inv_outputs", EnumDyeColor.PURPLE, "Output Items",
            BoundingRectangle.slots(115, 43, 3, 2),
            inputFilter = { _, _ -> false})
    }

    //#endregion

    override fun performWork(): Float {
        var result = 0.0f

        val water = this.waterTank.drain(250, false)
        val lava = this.lavaTank.drain(125, false)
        if (water != null && water.amount == 250 && lava != null && lava.amount == 125) {
            var cobble = ItemStack(Blocks.COBBLESTONE, 1)
            cobble = ItemStackUtil.insertItems(this.outputs, cobble, false)
            if (cobble.isEmpty) {
                this.waterTank.drain(250, true)
                this.lavaTank.drain(125, true)
                result = 0.25f
            }
        }

        var moved = true
        while (moved && result <= .85f) {
            moved = false
            for (x in 0..2) {
                var stack = this.inputs.extractItem(x, 1, true)
                if (stack.isEmpty) {
                    continue
                }
                stack = ItemStackUtil.insertItems(this.outputs, stack, false)
                if (stack.isEmpty) {
                    this.inputs.extractItem(x, 1, false)
                    result += .15f
                    moved = true
                    break
                }
            }
        }

        return result
    }
}
