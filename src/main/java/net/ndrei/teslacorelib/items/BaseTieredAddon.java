package net.ndrei.teslacorelib.items;

import net.minecraft.creativetab.CreativeTabs;
import net.ndrei.teslacorelib.tileentities.SidedTileEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CF on 2017-04-01.
 */
public class BaseTieredAddon extends BaseAddon {
    public BaseTieredAddon(String modId, CreativeTabs tab, String registryName) {
        super(modId, tab, registryName);
    }

    protected boolean hasSameFunction(BaseTieredAddon other) {
        return (other != null) &&
                (this.getClass().isAssignableFrom(other.getClass()) || (this.getAddonFunction().equals(other.getAddonFunction())));
    }

    protected String getAddonFunction() {
        return this.getRegistryName().toString();
    }

    protected int getTier() {
        return 1;
    }

    @Override
    public boolean canBeAddedTo(SidedTileEntity machine) {
        int tier = this.getTier();
        Map<Integer, BaseAddon> tiers = new HashMap<>();

        List<BaseAddon> addons = machine.getAddons();
        if (addons != null) {
            for (BaseAddon addon : addons) {
                if (!(addon instanceof BaseTieredAddon)) {
                    break;
                }
                BaseTieredAddon tiered = (BaseTieredAddon)addon;

                if (this.hasSameFunction(tiered)) {
                    if (tiered.getTier() == tier) {
                        // already has an addon with same tier and function
                        return false;
                    }

                    tiers.put(tiered.getTier(), tiered);
                }
            }
        }

        for(int index = 1; index < tier; index++) {
            if (!tiers.containsKey(index)) {
                // missing an addon with an inferior tier
                return false;
            }
        }
        return true;
    }
//
//    @Override
//    public void onRemoved(ItemStack addon, SidedTileEntity machine) {
//        super.onRemoved(addon, machine);
//
//        if (!ItemStackUtil.isEmpty(addon) && (addon.getItem() == this)) {
//            List<BaseAddon> addons = machine.getAddons();
//            if (addons != null) {
//                int tier = this.getTier();
//
//                for (BaseAddon ba : addons) {
//                    if (!(ba instanceof BaseTieredAddon)) {
//                        break;
//                    }
//                    BaseTieredAddon tiered = (BaseTieredAddon)ba;
//
//                    if (this.hasSameFunction(tiered)) {
//                        if (tiered.getTier() > tier) {
//                            // already has an addon with same tier and function
//                            machine.removeAddon(ba, true);
//                        }
//                    }
//                }
//            }
//        }
//    }
}
