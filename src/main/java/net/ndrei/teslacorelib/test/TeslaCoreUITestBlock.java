package net.ndrei.teslacorelib.test;

import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.blocks.OrientedBlock;

/**
 * Created by CF on 2016-12-21.
 */
public final class TeslaCoreUITestBlock extends OrientedBlock<TeslaCoreUITestEntity> {
    public TeslaCoreUITestBlock() {
        super(TeslaCoreLib.MODID, "test_machine", TeslaCoreUITestEntity.class);
    }
}
