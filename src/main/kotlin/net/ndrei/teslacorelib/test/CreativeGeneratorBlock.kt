package net.ndrei.teslacorelib.test

import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterBlock(TeslaCoreLibConfig.REGISTER_TEST_MACHINES)
object CreativeGeneratorBlock : OrientedBlock<CreativeGeneratorEntity>(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "creative_generator", CreativeGeneratorEntity::class.java)
