package net.ndrei.teslacorelib.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.TeslaCoreLib
import net.ndrei.teslacorelib.render.SidedTileEntityRenderer
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

/**
 * Created by CF on 2017-06-27.
 */
abstract class OrientedBlock<T : SidedTileEntity>
    protected constructor(modId: String, tab: CreativeTabs?, registryName: String, private val teClass: Class<T>, material: Material)
        : AxisAlignedBlock(modId, tab, registryName, material), ITileEntityProvider {
    protected constructor(modId: String, tab: CreativeTabs, registryName: String, teClass: Class<T>)
        : this(modId, tab, registryName, teClass, Material.ROCK)

    init {
        this.setHarvestLevel("pickaxe", 0)
        this.setHardness(3.0f)
    }

    override fun registerBlock(registry: IForgeRegistry<Block>) {
        super.registerBlock(registry)
        GameRegistry.registerTileEntity(this.teClass, this.registryName!!.toString() + "_tile")
    }

    @SideOnly(Side.CLIENT)
    override fun registerRenderer() {
        super.registerRenderer()

        ClientRegistry.bindTileEntitySpecialRenderer(this.teClass, this.specialRenderer)
    }

    @Deprecated("One should not override this.", ReplaceWith("One should use the SidedTileEntity.getRenderers!"), DeprecationLevel.WARNING)
    protected open val specialRenderer: TileEntitySpecialRenderer<SidedTileEntity>
        @SideOnly(Side.CLIENT)
        get() = SidedTileEntityRenderer

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return try {
            this.teClass.newInstance()
        } catch (e: InstantiationException) {
            TeslaCoreLib.logger.error(e)
            null
        } catch (e: IllegalAccessException) {
            TeslaCoreLib.logger.error(e)
            null
        }
    }
}
