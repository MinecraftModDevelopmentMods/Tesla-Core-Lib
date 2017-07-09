package net.ndrei.teslacorelib.items

import com.mojang.realmsclient.gui.ChatFormatting
import net.darkhax.tesla.api.implementation.BaseTeslaContainer
import net.darkhax.tesla.api.implementation.BaseTeslaContainerProvider
import net.darkhax.tesla.capability.TeslaCapabilities
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterItem

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterItem
object TeslaBattery : RegisteredItem(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "battery") {
    init {
        super
                .setHasSubtypes(true)
                .addPropertyOverride(ResourceLocation("power"), { stack, _, _ ->
                    val holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
                    if (holder == null) {
                        0.0f
                    }
                    else {
                        val thing = Math.round(holder.storedPower.toDouble() / holder.capacity.toDouble() * 5)
                        Math.max(0, Math.min(5, thing)) * .2f
                    }
                })
    }

//    override val recipe: IRecipe?
//        get() = ShapedOreRecipe(null, ItemStack(this, 1),
//                "IRI",
//                "RXR",
//                "IRI",
//                'I', "ingotIron",
//                'R', "dustRedstone",
//                'X', "blockRedstone"
//        )

    override fun initCapabilities(stack: ItemStack?, nbt: NBTTagCompound?): ICapabilityProvider? {
        return BaseTeslaContainerProvider(object : BaseTeslaContainer(10000, 100, 100) { })
    }

    override fun getItemStackLimit(stack: ItemStack?): Int {
        val tesla = stack!!.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
        return if (tesla == null || tesla.storedPower == 0L) 16 else 1
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack?, worldIn: World?, tooltip: MutableList<String>?, flagIn: ITooltipFlag?) {
        val holder = stack!!.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
        if (holder != null) {
            if (holder.storedPower > 0) {
                tooltip!!.add(ChatFormatting.AQUA.toString() + "Power: " + holder.storedPower + ChatFormatting.RESET + " of " + ChatFormatting.AQUA + holder.capacity)
            } else {
                tooltip!!.add(ChatFormatting.RED.toString() + "EMPTY!")
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (this.isInCreativeTab(tab)) {
            subItems.add(ItemStack(this))
            val full = ItemStack(this)
            val holder = full.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null)
            val consumer = full.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null)
            if (holder != null && consumer != null) {
                var cycle = 0 // just in case something goes wrong :)
                while (holder.capacity > holder.storedPower && cycle++ < 100) {
                    // fill it up
                    consumer.givePower(holder.capacity - holder.storedPower, false)
                }
            }
            subItems.add(full)
        }
    }
}
