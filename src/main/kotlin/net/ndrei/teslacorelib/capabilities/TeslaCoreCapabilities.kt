package net.ndrei.teslacorelib.capabilities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.ndrei.teslacorelib.capabilities.container.IGuiContainerProvider
import net.ndrei.teslacorelib.capabilities.wrench.ITeslaWrenchHandler
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.IGuiContainerPiece
import net.ndrei.teslacorelib.items.TeslaWrench

/**
 * Created by CF on 2017-06-28.
 */
object TeslaCoreCapabilities {
//    @CapabilityInject(IHudInfoProvider::class)
//    lateinit var CAPABILITY_HUD_INFO: Capability<IHudInfoProvider>

    @CapabilityInject(ITeslaWrenchHandler::class)
    lateinit var CAPABILITY_WRENCH: Capability<ITeslaWrenchHandler>

    @CapabilityInject(IGuiContainerProvider::class)
    lateinit var CAPABILITY_GUI_CONTAINER: Capability<IGuiContainerProvider>

//    internal class CapabilityHudInfoProvider<T : IHudInfoProvider> : Capability.IStorage<IHudInfoProvider> {
//        override fun writeNBT(capability: Capability<IHudInfoProvider>, instance: IHudInfoProvider, side: EnumFacing): NBTBase? {
//            return null
//        }
//
//        override fun readNBT(capability: Capability<IHudInfoProvider>, instance: IHudInfoProvider, side: EnumFacing, nbt: NBTBase) {}
//    }

    internal class CapabilityWrenchHandler<T : ITeslaWrenchHandler> : Capability.IStorage<ITeslaWrenchHandler> {
        override fun writeNBT(capability: Capability<ITeslaWrenchHandler>, instance: ITeslaWrenchHandler, side: EnumFacing): NBTBase? {
            return null
        }

        override fun readNBT(capability: Capability<ITeslaWrenchHandler>, instance: ITeslaWrenchHandler, side: EnumFacing, nbt: NBTBase) {}
    }

    internal class CapabilityGuiContainer<T : IGuiContainerProvider> : Capability.IStorage<IGuiContainerProvider> {
        override fun writeNBT(capability: Capability<IGuiContainerProvider>, instance: IGuiContainerProvider, side: EnumFacing): NBTBase? {
            return null
        }

        override fun readNBT(capability: Capability<IGuiContainerProvider>, instance: IGuiContainerProvider, side: EnumFacing, nbt: NBTBase) {}
    }

//    internal class DefaultHudInfo : IHudInfoProvider {
//        override val hudLines: List<HudInfoLine>
//            get() = listOf()
//    }

    internal class DefaultWrenchHandler : ITeslaWrenchHandler {
        override fun onWrenchUse(wrench: TeslaWrench, player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
            return EnumActionResult.PASS
        }
    }

    internal class DefaultGuiContainer : IGuiContainerProvider {
        override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece> {
            return mutableListOf()
        }

        override fun getSlots(container: BasicTeslaContainer<*>): MutableList<Slot> {
            return mutableListOf()
        }

        override fun getContainer(id: Int, player: EntityPlayer): BasicTeslaContainer<*>? {
            return null
        }

        override fun getGuiContainer(id: Int, player: EntityPlayer): BasicTeslaGuiContainer<*>? {
            return null
        }
    }

    fun register() {
//        CapabilityManager.INSTANCE.register(IHudInfoProvider::class.java, TeslaCoreCapabilities.CapabilityHudInfoProvider<IHudInfoProvider>(), DefaultHudInfo::class.java)
        CapabilityManager.INSTANCE.register(ITeslaWrenchHandler::class.java, TeslaCoreCapabilities.CapabilityWrenchHandler<ITeslaWrenchHandler>(), DefaultWrenchHandler::class.java)
        CapabilityManager.INSTANCE.register(IGuiContainerProvider::class.java, TeslaCoreCapabilities.CapabilityGuiContainer<IGuiContainerProvider>(), DefaultGuiContainer::class.java)
    }
}
