package net.ndrei.teslacorelib.items;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.api.implementation.BaseTeslaContainerProvider;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.ndrei.teslacorelib.TeslaCoreLib;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CF on 2016-12-30.
 */
public class TeslaBattery extends RegisteredItem {
    public TeslaBattery() {
        super(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "battery");

        super
                .setHasSubtypes(true)
                .addPropertyOverride(new ResourceLocation("power"), (stack, worldIn, entityIn) -> {
                    ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
                    if (holder == null) {
                        return 0.0f;
                    }

                    long thing = Math.round((double) holder.getStoredPower() / (double) holder.getCapacity() * 5);
                    return (float) Math.max(0, Math.min(5, thing)) * .2f;
                });
    }

    @Override
    protected IRecipe getRecipe() {
        return new ShapedOreRecipe(new ItemStack(this, 1),
                "IRI", "RXR", "IRI",
                'I', Items.IRON_INGOT,
                'R', Items.REDSTONE,
                'X', Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new BaseTeslaContainerProvider(new BaseTeslaContainer(10000, 100, 100) {
        });
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        ITeslaHolder tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
        if ((tesla == null) || (tesla.getStoredPower() == 0)) {
            return 16;
        }
        return 1;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
        if (holder != null) {
            if (holder.getStoredPower() > 0) {
                tooltip.add(ChatFormatting.AQUA + "Power: " + holder.getStoredPower() + ChatFormatting.RESET + " of " + ChatFormatting.AQUA + holder.getCapacity());
            } else {
                tooltip.add(ChatFormatting.RED + "EMPTY!");
            }
        }
        super.addInformation(stack, playerIn, tooltip, advanced);
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        subItems.add(new ItemStack(itemIn));
        ItemStack full = new ItemStack(itemIn);
        ITeslaHolder holder = full.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
        ITeslaConsumer consumer = full.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null);
        if ((holder != null) && (consumer != null)) {
            int cycle = 0; // just in case something goes wrong :)
            while ((holder.getCapacity() > holder.getStoredPower()) && (cycle++ < 100)){
                // fill it up
                consumer.givePower(holder.getCapacity() - holder.getStoredPower(), false);
            }
        }
        subItems.add(full);
    }
}
