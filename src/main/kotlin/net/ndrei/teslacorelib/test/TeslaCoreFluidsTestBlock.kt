package net.ndrei.teslacorelib.test

import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock
import net.ndrei.teslacorelib.config.TeslaCoreLibConfig

/**
 * Created by CF on 2017-06-27.
 */
@AutoRegisterBlock(TeslaCoreLibConfig.REGISTER_TEST_MACHINES)
object TeslaCoreFluidsTestBlock : OrientedBlock<TeslaCoreFluidsTestEntity>(TeslaCoreLib.MODID, TeslaCoreLib.creativeTab, "test_machine2", TeslaCoreFluidsTestEntity::class.java)
