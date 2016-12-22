package net.ndrei.teslacorelib.test;

import net.ndrei.teslacorelib.tileentities.ElectricInventoryTileEntity;

/**
 * Created by CF on 2016-12-21.
 */
public final class TeslaCoreUITestEntity extends ElectricInventoryTileEntity {
    public TeslaCoreUITestEntity() {
        super(-1);
    }

    @Override
    protected float performWork() {
        return 0;
    }
}
