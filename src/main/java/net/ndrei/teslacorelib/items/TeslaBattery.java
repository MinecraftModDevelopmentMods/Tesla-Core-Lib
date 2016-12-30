package net.ndrei.teslacorelib.items;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;
import net.darkhax.tesla.api.implementation.BaseTeslaContainerProvider;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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

//        super.setMaxDamage(100);
        super.addPropertyOverride(new ResourceLocation("power"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                ITeslaHolder holder = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
                if (holder == null) {
                    return 0.0f;
                }

                long thing = Math.round((double)holder.getStoredPower() / (double)holder.getCapacity() * 5);
                return (float)Math.max(0, Math.min(5, thing)) * .2f;
            }
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
//            private ItemStack targetStack;
//
//            BaseTeslaContainer initializeStack(ItemStack targetStack) {
//                this.targetStack = targetStack;
//                return this;
//            }

//            @Override
//            public long givePower(long power, boolean simulated) {
//                if (this.targetStack != null) {
//                    TeslaBattery.this.setDamage(this.targetStack, 0);
//                    return super.givePower(power, simulated);
//                }
//                return 0;
//            }

//            @Override
//            public long takePower(long power, boolean simulated) {
//                if (this.targetStack != null) {
//                    TeslaBattery.this.setDamage(this.targetStack, 0);
//                    return super.takePower(power, simulated);
//                }
//                return 0;
//            }
        }/*.initializeStack(stack)*/);
    }

//    @Override
//    public void setDamage(ItemStack stack, int damage) {
//        ITeslaHolder tesla = stack.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, null);
//        damage = (tesla.getCapacity() > 0) ? (100 - Math.round(tesla.getStoredPower() * 100f / tesla.getCapacity())) : 0;
//        super.setDamage(stack, (damage == 100) ? 0 : damage);
//    }

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
//
//    @Override
//    public boolean isRepairable() {
//        return false;
//    }
}
