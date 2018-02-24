package net.ndrei.teslacorelib.inventory

import com.google.common.collect.Lists
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.IFluidTank
import net.minecraftforge.fluids.capability.FluidTankProperties
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.utils.canFillFrom
import net.ndrei.teslacorelib.utils.fillFrom

/**
 * Created by CF on 2017-06-28.
 */
open class FluidStorage : IFluidHandler, INBTSerializable<NBTTagCompound> {
    protected val tanks: MutableList<IFluidTank> = mutableListOf()

    //#region IFluidHandler Methods

    override fun getTankProperties(): Array<IFluidTankProperties> {
        val list = Lists.newArrayList<IFluidTankProperties>()

        for (tank in this.tanks) {
            var canDrain = true
            var canFill = true
            if (tank is IFilteredFluidTank) {
                canDrain = tank.canDrain()
                canFill = tank.canFill()
            }
            list.add(FluidTankProperties(tank.fluid, tank.capacity, canFill, canDrain))
        }

        return list.toTypedArray()
    }

    override fun fill(resource: FluidStack?, doFill: Boolean): Int {
        if (resource == null) {
            return 0
        }

        val source = resource.copy()
        var used = 0

        for (tank in this.tanks) {
            if (tank is IFilteredFluidTank) {
                if (!tank.canFill() || !tank.acceptsFluid(source)) {
                    continue
                }
            }

            val amount = tank.fill(source, doFill)
            used += Math.min(amount, source.amount)
            if (source.amount <= amount) {
                break
            }
            source.amount -= amount
        }

        // TeslaCoreLib.logger.info("Tank filled with " + used + " mb of " + source.getFluid().getName());
        return used
    }

    override fun drain(resource: FluidStack?, doDrain: Boolean): FluidStack? {
        if (resource == null) {
            return null
        }

        return this.drain(resource.amount, doDrain, resource)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        return this.drain(maxDrain, doDrain, null)
    }

    private fun drain(maxDrain: Int, doDrain: Boolean, filter: FluidStack?): FluidStack? {
        var currentFilter = filter
        var fluid: FluidStack? = if (currentFilter == null) null else currentFilter.copy()
        if (fluid != null) {
            fluid.amount = 0
        }

        for (tank in this.tanks) {
            if (tank is IFilteredFluidTank) {
                if (!tank.canDrain()) {
                    continue
                }
            }

            val contained = tank.fluid ?: continue

            if (currentFilter != null && !contained.isFluidEqual(currentFilter)) {
                continue
            }

            val drained = tank.drain(maxDrain - (if (fluid == null) 0 else fluid.amount), doDrain) ?: continue

            if (fluid == null) {
                fluid = drained.copy()
                if (currentFilter == null) {
                    // make sure we are not extracting different fluids from other tanks
                    currentFilter = fluid
                }
            } else {
                fluid.amount += drained.amount
            }

            if ((fluid != null) && (fluid.amount >= maxDrain)) {
                break
            }
        }

        return if (fluid != null && fluid.amount == 0) null else fluid // TODO: FluidStack.EMPTY ??
    }

    //#endregion

    fun getTanks(): Array<IFluidTank> {
        return this.tanks.toTypedArray()
    }

    fun addTank(tank: IFluidTank) {
        this.tanks.add(tank)
    }

    @Suppress("unused")
    fun addTank(acceptedFluid: Fluid, capacity: Int): FilteredFluidTank {
        val tank = FilteredFluidTank(acceptedFluid, FluidTank(capacity))
        this.addTank(tank)
        return tank
    }

    fun addTank(acceptedFluid: Fluid, tank: IFluidTank, color: EnumDyeColor, name: String, boundingBox: BoundingRectangle): ColoredFluidHandler {
        val colored = ColoredFluidHandler(acceptedFluid, tank, color, name, boundingBox)
        this.addTank(colored)
        return colored
    }

    fun removeTank(tank: IFluidTank) {
        this.tanks.removeIf { t -> FluidStorage.isSameTank(tank, t) }
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()

        val list = NBTTagList()
        for (rawTank in this.tanks) {
            val tank = (rawTank as? FilteredFluidTank)?.innerTank ?: rawTank
            val tankNbt: NBTTagCompound
            if (tank is ISerializableFluidTank) {
                tankNbt = tank.serializeNBT()
            } else {
                tankNbt = NBTTagCompound()
                val fluid = tank.fluid
                if (fluid != null) {
                    fluid.writeToNBT(tankNbt)
                } else {
                    tankNbt.setBoolean("_empty", true)
                }
            }
            list.appendTag(tankNbt)
        }
        nbt.setTag("tanks", list)

        return nbt
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val list: NBTTagList? = if (nbt.hasKey("tanks", Constants.NBT.TAG_LIST))
            nbt.getTagList("tanks", Constants.NBT.TAG_COMPOUND) else null
        if (list != null) {
            var index = 0
            while (index < list.tagCount() && index < this.tanks.size) {
                var tank = this.tanks[index]
                if (tank is FilteredFluidTank) {
                    tank = tank.innerTank
                }
                val tankNbt = list.getCompoundTagAt(index)
                if (tank is ISerializableFluidTank) {
                    tank.deserializeNBT(tankNbt)
                } else {
                    val fluid: FluidStack? = if (tankNbt.hasKey("_empty")) {
                        null
                    } else {
                        FluidStack.loadFluidStackFromNBT(tankNbt)
                    }
                    if (tank.fluidAmount > 0) {
                        // drain the tank
                        tank.drain(tank.fluidAmount, true)
                    }
                    if (fluid != null) {
                        val filled = tank.fill(fluid, true)
                        if (filled != fluid.amount) {
                            TeslaCoreLib.logger.warn("FluidTank deserialized fluid wasn't totally put into tank: " + fluid.toString())
                        }
                    }
                }
                index++
            }
        }
    }

    fun tankCount() = this.tanks.size

    fun acceptsFluidFrom(bucket: ItemStack) = this.tanks.canFillFrom(bucket)
//            : Boolean {
//        if (!bucket.isEmpty && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
//            val handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
//            val fluid = handler?.drain(1000, false)
//            if (fluid != null && fluid.amount > 0) {
//                return 1000 == this.fill(fluid, false)
//            }
//        }
//        return false
//    }

    @Suppress("unused")
    fun fillFluidFrom(bucket: ItemStack): ItemStack = this.tanks.fillFrom(bucket)
//            : ItemStack {
//        if (!ItemStackUtil.isEmpty(bucket) && bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
//            val clone = bucket.copy()
//            val handler = clone.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
//            val fluid = handler?.drain(Fluid.BUCKET_VOLUME, false)
//            if (fluid != null && fluid.amount == Fluid.BUCKET_VOLUME) {
//                val filled = this.fill(fluid, false)
//                if (filled == Fluid.BUCKET_VOLUME) {
//                    this.fill(fluid, true)
//                    handler.drain(filled, true)
//                    return handler.container
//                }
//            }
//        }
//        return bucket
//    }

    companion object {
        private fun isSameTank(a: IFluidTank?, b: IFluidTank?): Boolean {
            if (a == b) {
                return true
            } else if (a != null && b != null && b is IFluidTankWrapper) {
                return FluidStorage.isSameTank(a, (b as IFluidTankWrapper).innerTank)
            }
            return false
        }
    }
}
