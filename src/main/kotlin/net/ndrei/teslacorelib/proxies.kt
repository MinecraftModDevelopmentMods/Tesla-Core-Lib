package net.ndrei.teslacorelib

import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.ndrei.teslacorelib.annotations.processInitAnnotations
import net.ndrei.teslacorelib.annotations.processPostInitAnnotations
import net.ndrei.teslacorelib.annotations.processPreInitAnnotations
import net.ndrei.teslacorelib.capabilities.TeslaCoreCapabilities
import net.ndrei.teslacorelib.gui.TeslaCoreGuiProxy

/**
 * Created by CF on 2017-06-27.
 */
abstract class BaseProxy(val side: Side) {
    protected lateinit var asm: ASMDataTable

    @SuppressWarnings("unused")
    open fun preInit(ev: FMLPreInitializationEvent) {
        this.asm = ev.asmData
        processPreInitAnnotations(ev.asmData)
    }

    @SuppressWarnings("unused")
    open fun init(ev: FMLInitializationEvent) {
        processInitAnnotations(this.asm)
    }

    @SuppressWarnings("unused")
    open fun postInit(ev: FMLPostInitializationEvent) {
        processPostInitAnnotations(this.asm)
    }
}

abstract class CommonProxy(side: Side): BaseProxy(side) {
    override fun preInit(ev: FMLPreInitializationEvent) {
        super.preInit(ev)

        TeslaCoreCapabilities.register()
        NetworkRegistry.INSTANCE.registerGuiHandler(TeslaCoreLib.instance, TeslaCoreGuiProxy())
    }
}

@Suppress("unused")
@SideOnly(Side.CLIENT)
class ClientProxy: CommonProxy(Side.CLIENT)

@Suppress("unused")
@SideOnly(Side.SERVER)
class ServerProxy: CommonProxy(Side.SERVER)