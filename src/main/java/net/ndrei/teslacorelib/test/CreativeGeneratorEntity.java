package net.ndrei.teslacorelib.test;

import net.ndrei.teslacorelib.tileentities.ElectricGenerator;

/**
 * Created by CF on 2016-12-27.
 */
public class CreativeGeneratorEntity extends ElectricGenerator {
    public CreativeGeneratorEntity() {
        super(-2);
    }

    @Override
    protected long consumeFuel() {
        return 50000;
    }
}
