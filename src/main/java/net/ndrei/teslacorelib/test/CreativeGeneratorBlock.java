package net.ndrei.teslacorelib.test;

import net.ndrei.teslacorelib.TeslaCoreLib;
import net.ndrei.teslacorelib.blocks.OrientedBlock;

/**
 * Created by CF on 2016-12-27.
 */
public class CreativeGeneratorBlock extends OrientedBlock<CreativeGeneratorEntity> {
    public CreativeGeneratorBlock() {
        super(TeslaCoreLib.MODID, "creative_generator", CreativeGeneratorEntity.class);
    }
}
