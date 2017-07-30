package net.ndrei.teslacorelib.render.selfrendering

import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.model.TRSRTransformation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface ISelfRenderingBlock {
    fun getRegistryName(): ResourceLocation? // to be compatible with the Block method

    @SideOnly(Side.CLIENT)
    fun getTextures() = listOf<ResourceLocation>()
    @SideOnly(Side.CLIENT)
    fun getBakeries(layer: BlockRenderLayer?, state: IBlockState?, stack: ItemStack?, side: EnumFacing?, rand: Long, transform: TRSRTransformation)
            = listOf<IBakery>()

    @SideOnly(Side.CLIENT)
    fun getParticleTexture() = this.getTextures().firstOrNull()

    @SideOnly(Side.CLIENT)
    fun renderTESR(proxy: TESRProxy, te: TileEntity, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)
    {}
}
