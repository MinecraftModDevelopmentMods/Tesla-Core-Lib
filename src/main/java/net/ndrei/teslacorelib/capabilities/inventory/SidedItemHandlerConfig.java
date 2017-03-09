package net.ndrei.teslacorelib.capabilities.inventory;

import com.google.common.collect.Lists;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.ndrei.teslacorelib.inventory.BoundingRectangle;
import net.ndrei.teslacorelib.inventory.ColoredItemHandlerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by CF on 2016-12-17.
 */
public class SidedItemHandlerConfig implements ISidedItemHandlerConfig, INBTSerializable<NBTTagList> {
    private Map<EnumDyeColor, List<EnumFacing>> facesConfig;
    private List<ColoredItemHandlerInfo> information;

    public SidedItemHandlerConfig() {
        this.facesConfig = new HashMap<>();
    }

//    public SidedItemHandlerConfig(List<ColoredItemHandlerInfo> info) {
//        this();
//        this.setColoredInfo(info);
//    }

    public boolean isSideSet(EnumDyeColor color, EnumFacing side) {
        if (this.facesConfig.containsKey(color)) {
            List<EnumFacing> list = this.facesConfig.get(color);
            if ((list != null) && list.contains(side)) {
                for(EnumFacing facing : list) {
                    if (facing == side) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean toggleSide(EnumDyeColor color, EnumFacing side) {
        if (this.facesConfig.containsKey(color)) {
            List<EnumFacing> list = this.facesConfig.get(color);
            if ((list != null) && list.contains(side)) {
                list.remove(side);
            } else if (list != null) {
                list.add(side);
            } else /*if (list == null)*/ {
                this.setSidesForColor(color, Lists.newArrayList(side));
            }
        } else {
            this.setSidesForColor(color, Lists.newArrayList(side));
        }
        this.updated();
        return this.isSideSet(color, side);
    }

    @Override
    public List<ColoredItemHandlerInfo> getColoredInfo() {
        return (this.information == null) ? Lists.newArrayList() : this.information;
    }

//    public void setColoredInfo(List<ColoredItemHandlerInfo> info) {
//        this.information = info;
//    }

    public void addColoredInfo(String name, EnumDyeColor color, BoundingRectangle highlight) {
        this.addColoredInfo(new ColoredItemHandlerInfo(name, color, highlight));
    }

    public void addColoredInfo(ColoredItemHandlerInfo info) {
        if (this.information == null) {
            this.information = Lists.newArrayList();
        }
        this.information.add(info);
    }

    @Override
    public List<EnumFacing> getSidesForColor(EnumDyeColor color) {
        List<EnumFacing> list;

        if (this.facesConfig.containsKey(color)) {
            list = this.facesConfig.get(color);
        }
        else {
            list = Lists.newArrayList();
        }

        return list;
    }

    @Override
    public void setSidesForColor(EnumDyeColor color, List<EnumFacing> sides) {
        if (sides == null) {
            sides = Lists.newArrayList();
        }
        this.facesConfig.put(color, sides);
        this.updated();
    }

    @Override
    public NBTTagList serializeNBT() {
        NBTTagList list = new NBTTagList();

        EnumDyeColor[] keys = this.facesConfig.keySet().toArray(new EnumDyeColor[0]);
        for(int k = 0; k < keys.length; k++) {
            NBTTagCompound nbt = new NBTTagCompound();

            nbt.setInteger("color", keys[k].getMetadata());
            NBTTagList sides = new NBTTagList();
            for(EnumFacing facing: this.facesConfig.get(keys[k])) {
                sides.appendTag(new NBTTagInt(facing.getIndex()));
            }

            nbt.setTag("sides", sides);
            list.appendTag(nbt);
        }

        return list;
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {
        this.facesConfig.clear();
        for(int i = 0; i < nbt.tagCount(); i++) {
            NBTTagCompound item = nbt.getCompoundTagAt(i);
            EnumDyeColor color = EnumDyeColor.byMetadata(item.getInteger("color"));
            List<EnumFacing> sides = Lists.newArrayList();
            NBTTagList list = item.getTagList("sides", Constants.NBT.TAG_INT);
            for(int j = 0; j < list.tagCount(); j++) {
                sides.add(EnumFacing.getFront(list.getIntAt(j)));
            }
            this.facesConfig.put(color, sides);
        }
        this.updated();
    }

    protected void updated() {}

    @Override
    public void removeColoredInfo(EnumDyeColor color) {
        this.information.removeIf(i -> (i.getColor() == color));
        this.facesConfig.remove(color);
        
        this.updated();
    }
}
