package net.ndrei.teslacorelib.inventory;

import com.google.common.collect.Lists;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.ndrei.teslacorelib.capabilities.inventory.ISidedItemHandlerConfig;
import net.ndrei.teslacorelib.capabilities.inventory.SidedItemHandlerConfig;

import java.util.*;

/**
 * Created by CF on 2016-12-15.
 */
public class SidedItemHandler extends MultiItemHandler implements ISidedItemHandler {
    // private Map<EnumDyeColor, List<EnumFacing>> facesConfig;
    private SidedItemHandlerConfig sidedConfig = null;

    public SidedItemHandler(SidedItemHandlerConfig config) {
        this(null, config);
    }

    public SidedItemHandler(List<IItemHandler> handlers, SidedItemHandlerConfig config) {
        super(handlers);

        // this.facesConfig = new HashMap<>();
        this.sidedConfig = config;
    }

//    @Override
//    public List<ColoredItemHandlerInfo> getColoredInfo() {
//        List<ColoredItemHandlerInfo> result = Lists.newArrayList();
//
//        for(int i = 0; i < this.getInventories(); i++) {
//            IItemHandler handler = this.getInventory(i);
//            if (handler instanceof ColoredItemHandler) {
//                result.add(((ColoredItemHandler)handler).getInfo());
//            }
//        }
//
//        return result;
//    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] result = new int[0];

        int index = 0;
        for(int i = 0; i < this.getInventories(); i++) {
            IItemHandler handler = this.getInventory(i);
            int size = handler.getSlots();
            if (handler instanceof ColoredItemHandler) {
                EnumDyeColor color = ((ColoredItemHandler)handler).getColor();
                if (this.sidedConfig.isSideSet(color, side)) {
                    int startIndex = result.length;
                    result = Arrays.copyOf(result, result.length + size);
                    for(int x = 0; x < size; x++) {
                        result[startIndex + x] = index + x;
                    }
                }
            }
            index += size;
        }

        return result;
    }

//    @Override
//    public NBTTagList serializeNBT() {
//        NBTTagList list = new NBTTagList();
//
//        EnumDyeColor[] keys = this.facesConfig.keySet().toArray(new EnumDyeColor[0]);
//        for(int k = 0; k < keys.length; k++) {
//            NBTTagCompound nbt = new NBTTagCompound();
//
//            nbt.setInteger("color", keys[0].getMetadata());
//            NBTTagList sides = new NBTTagList();
//            for(EnumFacing facing: this.facesConfig.get(keys[k])) {
//                sides.appendTag(new NBTTagInt(facing.getIndex()));
//            }
//
//            nbt.setTag("sides", sides);
//            list.appendTag(nbt);
//        }
//
//        return list;
//    }
//
//    @Override
//    public void deserializeNBT(NBTTagList nbt) {
//        this.facesConfig.clear();
//        for(int i = 0; i < nbt.tagCount(); i++) {
//            NBTTagCompound item = nbt.getCompoundTagAt(i);
//            EnumDyeColor color = EnumDyeColor.byMetadata(item.getInteger("color"));
//            List<EnumFacing> sides = Lists.newArrayList();
//            NBTTagList list = item.getTagList("sides", Constants.NBT.TAG_INT);
//            for(int j = 0; j < list.tagCount(); j++) {
//                sides.add(EnumFacing.getFront(list.getIntAt(j)));
//            }
//            this.facesConfig.put(color, sides);
//        }
//    }
}
