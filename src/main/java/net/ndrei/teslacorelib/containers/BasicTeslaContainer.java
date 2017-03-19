package net.ndrei.teslacorelib.containers;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.ndrei.teslacorelib.compatibility.ItemStackUtil;
import net.ndrei.teslacorelib.tileentities.ElectricTileEntity;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CF on 2016-12-18.
 */
public class BasicTeslaContainer<T extends SidedTileEntity> extends Container {
    private T entity;

    private int entitySlots = 0;
    private int playerSlots = 0, playerExtraSlots = 0, playerQuickSlots = 0;

    private EntityPlayer player;

    public BasicTeslaContainer(T entity, EntityPlayer player) {
        this.entity = entity;
        this.player = player;

        List<Slot> slots = this.entity.getSlots(this);
        if (slots != null) {
            this.entitySlots = slots.size();
            for (Slot slot : slots) {
                this.addSlotToContainer(slot);
            }
        }

        if (player != null) {
            this.playerExtraSlots = this.addPlayerExtraSlots(player);
            this.playerQuickSlots = this.addPlayerQuickBar(player);
            this.playerSlots = this.addPlayerInventory(player);
        }
    }

    //#region player inventory

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !this.entity.isInvalid() && playerIn.getDistanceSq(this.entity.getPos().add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    protected int addPlayerQuickBar(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        IInventory playerInventory = player.inventory;

        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 160));
        }
        return 9;
    }

    protected int addPlayerInventory(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        IInventory playerInventory = player.inventory;

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(playerInventory, x + (y + 1) * 9, 8 + x * 18, 102 + y * 18));
            }
        }
        return 9 * 3;
    }

    //region armor and off hand

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{
            EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    protected int addPlayerExtraSlots(EntityPlayer player) {
        if (player == null) {
            return 0;
        }
        IInventory playerInventory = player.inventory;
        for (int k = 0; k < 4; ++k) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            this.addSlotToContainer(new Slot(playerInventory, 36 + (3 - k), 174, 84 + k * 18) {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit() {
                    return 1;
                }

                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace
                 * fuel.
                 */
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                }

                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(EntityPlayer playerIn) {
                    ItemStack itemstack = this.getStack();
                    return !(!ItemStackUtil.isEmpty(itemstack) && !playerIn.isCreative()
                            /*&& EnchantmentHelper.hasBindingCurse(itemstack)*/)
                            && super.canTakeStack(playerIn);
                }

                @Nullable
                @SideOnly(Side.CLIENT)
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            });
        }

        this.addSlotToContainer(new Slot(playerInventory, 40, 174, 160) {
            @Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        return 5;
    }

    //endregion

    public boolean hasPlayerInventory() {
        return (this.playerSlots > 0);
    }

    public void hidePlayerInventory() {
        while (this.playerSlots > 0) {
            this.inventorySlots.remove(this.inventorySlots.size() - 1);
            this.playerSlots--;
        }
    }

    public void showPlayerInventory() {
        if ((this.playerSlots == 0) && (this.player != null)) {
            this.playerSlots = this.addPlayerInventory(this.player);
        }
    }

    //#endregion

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack copyStack = ItemStackUtil.getEmptyStack();

        Slot slot = this.inventorySlots.get(index);
        if ((slot != null) && (slot.getHasStack())) {
            ItemStack origStack = slot.getStack();
            copyStack = origStack.copy();

            boolean merged = false;
            for(SlotRange range: this.getSlotsRange(index)) {
                if (super.mergeItemStack(origStack, range.start, range.end, range.reverse)) {
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                return ItemStackUtil.getEmptyStack();
            }
        }

        return copyStack;
    }

    private List<SlotRange> getSlotsRange(int sourceIndex) {
        int slots = this.inventorySlots.size();
        int playerSlots = this.playerSlots + this.playerQuickSlots + this.playerExtraSlots;
        int containerSlots = slots - playerSlots;

        List<SlotRange> list = Lists.newArrayList();

        if (sourceIndex < containerSlots) {
            // transfer from container to player
            // slot order is [container] -> [armor + shield] -> [hot bar] -> [inventory]
            if (this.playerSlots > 0) {
                // container -> player inventory
                list.add(new SlotRange(slots - this.playerSlots, slots, false));
            }

            // container -> player armor slots
            list.add(new SlotRange(containerSlots, containerSlots + this.playerExtraSlots - 1, false));

            // container -> player hot bar
            list.add(new SlotRange(
                    containerSlots + this.playerExtraSlots,
                    containerSlots + this.playerExtraSlots + this.playerQuickSlots, true));

            // container -> player shield slot
            list.add(new SlotRange(containerSlots + this.playerExtraSlots - 1, containerSlots + this.playerExtraSlots, false));
        } else {
            // transfer from player to container
            // player -> container
            list.add(new SlotRange(0, containerSlots, false));
        }

        return list;
    }

    private final class SlotRange {
        public final int start;
        public final int end;
        public final boolean reverse;

        public SlotRange(int start, int end, boolean reverse) {
            this.start = start;
            this.end = end;
            this.reverse = reverse;
        }
    }
}
