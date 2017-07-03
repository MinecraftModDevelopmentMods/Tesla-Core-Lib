package net.ndrei.teslacorelib.test

import net.minecraft.init.Blocks
import net.minecraft.inventory.Slot
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler
import net.ndrei.teslacorelib.compatibility.ItemStackUtil
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.containers.FilteredSlot
import net.ndrei.teslacorelib.gui.*
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.inventory.ColoredItemHandler
import net.ndrei.teslacorelib.inventory.LockableItemHandler
import net.ndrei.teslacorelib.tileentities.ElectricMachine

/**
 * Created by CF on 2017-06-27.
 */
class TeslaCoreUITestEntity : ElectricMachine(-1) {
    private var waterTank: IFluidTank? = null
    private var lavaTank: IFluidTank? = null

    private lateinit var inputs: LockableItemHandler
    private lateinit var outputs: ItemStackHandler

    //#region inventories       methods

    override fun initializeInventories() {
        super.initializeInventories()

        this.waterTank = super.addFluidTank(FluidRegistry.WATER, 5000, EnumDyeColor.BLUE, "Water Tank",
                BoundingRectangle(43, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))
        this.lavaTank = super.addFluidTank(FluidRegistry.LAVA, 5000, EnumDyeColor.RED, "Lava Tank",
                BoundingRectangle(43 + 18, 25, FluidTankPiece.WIDTH, FluidTankPiece.HEIGHT))
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

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = super.getSlots(container)

                val box = this.boundingBox
                for (x in 0..2) {
                    slots.add(FilteredSlot(this.itemHandlerForContainer, x, box.left + 1 + x * 18, box.top + 1))
                }

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = super.getGuiContainerPieces(container)

                val box = this.boundingBox
                pieces.add(TiledRenderedGuiPiece(box.left, box.top, 18, 18,
                        3, 1,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.GREEN))

                pieces.add(LockedInventoryTogglePiece(box.left - 16, box.top + 2, this@TeslaCoreUITestEntity, EnumDyeColor.GREEN))

                return pieces
            }
        })

        this.outputs = object : ItemStackHandler(6) {
            override fun onContentsChanged(slot: Int) {
                this@TeslaCoreUITestEntity.markDirty()
            }
        }
        super.addInventory(object : ColoredItemHandler(this.outputs!!, EnumDyeColor.PURPLE, "Output Items", BoundingRectangle(115, 43, 54, 36)) {
            override fun canInsertItem(slot: Int, stack: ItemStack): Boolean {
                return false
            }

            override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
                val slots = super.getSlots(container)

                val box = this.boundingBox
                for (x in 0..2) {
                    for (y in 0..1) {
                        slots.add(FilteredSlot(this.itemHandlerForContainer, y * 3 + x,
                                box.left + 1 + x * 18, box.top + 1 + y * 18))
                    }
                }

                return slots
            }

            override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
                val pieces = super.getGuiContainerPieces(container)

                val box = this.boundingBox
                pieces.add(TiledRenderedGuiPiece(box.left, box.top, 18, 18,
                        3, 2,
                        BasicTeslaGuiContainer.MACHINE_BACKGROUND, 108, 225, EnumDyeColor.PURPLE))

                return pieces
            }
        })
    }

    //#endregion
    //#region write/read/sync   methods

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        var compound = compound
        compound = super.writeToNBT(compound)

        if (this.inputs != null) {
            val nbt = this.inputs!!.serializeNBT()
            if (nbt != null) {
                compound.setTag("inv_inputs", nbt)
            }
        }

        if (this.outputs != null) {
            val nbt = this.outputs!!.serializeNBT()
            if (nbt != null) {
                compound.setTag("inv_outputs", nbt)
            }
        }

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        if (compound.hasKey("inv_inputs", Constants.NBT.TAG_COMPOUND) && this.inputs != null) {
            this.inputs!!.deserializeNBT(compound.getCompoundTag("inv_inputs"))
        }

        if (compound.hasKey("inv_outputs", Constants.NBT.TAG_COMPOUND) && this.outputs != null) {
            this.outputs!!.deserializeNBT(compound.getCompoundTag("inv_outputs"))
        }
    }

    //#endregion

    override fun performWork(): Float {
        var result = 0.0f

        if (this.waterTank != null && this.lavaTank != null && this.outputs != null) {
            val water = this.waterTank!!.drain(250, false)
            val lava = this.lavaTank!!.drain(125, false)
            if (water != null && water.amount == 250 && lava != null && lava.amount == 125) {
                var cobble = ItemStack(Blocks.COBBLESTONE, 1)
                cobble = ItemHandlerHelper.insertItem(this.outputs, cobble, false)
                if (ItemStackUtil.isEmpty(cobble)) {
                    this.waterTank!!.drain(250, true)
                    this.lavaTank!!.drain(125, true)
                    result = 0.25f
                }
            }
        }

        if (this.inputs != null && this.outputs != null) {
            var moved = true
            while (moved && result <= .85f) {
                moved = false
                for (x in 0..2) {
                    var stack = this.inputs!!.extractItem(x, 1, true)
                    if (ItemStackUtil.isEmpty(stack)) {
                        continue
                    }
                    stack = ItemHandlerHelper.insertItem(this.outputs, stack, false)
                    if (ItemStackUtil.isEmpty(stack)) {
                        this.inputs!!.extractItem(x, 1, false)
                        result += .15f
                        moved = true
                        break
                    }
                }
            }
        }

        return result
    }
}
