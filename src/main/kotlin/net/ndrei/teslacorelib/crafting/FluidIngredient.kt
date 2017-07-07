package net.ndrei.teslacorelib.crafting

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem

/**
 * Created by CF on 2017-07-07.
 */
class FluidIngredient(val fluid: Fluid) : Ingredient(0) {
    override fun getMatchingStacks(): Array<ItemStack>
        = arrayOf(FluidUtil.getFilledBucket(FluidStack(this.fluid, Fluid.BUCKET_VOLUME)))

    override fun apply(stack: ItemStack?): Boolean {
        if ((stack == null) || stack.isEmpty || !stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            return false
        }

        val handler: IFluidHandlerItem = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) ?: return false

        return handler.tankProperties.any {
            it.canDrainFluidType(FluidStack(this.fluid, Fluid.BUCKET_VOLUME))
            && it.capacity >= Fluid.BUCKET_VOLUME
        }
    }
}
