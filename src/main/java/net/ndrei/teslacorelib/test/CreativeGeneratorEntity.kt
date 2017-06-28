package net.ndrei.teslacorelib.test

import net.ndrei.teslacorelib.tileentities.ElectricGenerator

/**
 * Created by CF on 2017-06-27.
 */
class CreativeGeneratorEntity : ElectricGenerator(-2) {
    override fun consumeFuel(): Long {
        return 50000
    }
}