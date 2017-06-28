package net.ndrei.teslacorelib.test

import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterBlock
object CreativeGeneratorBlock : OrientedBlock<CreativeGeneratorEntity>(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "creative_generator", CreativeGeneratorEntity::class.java)