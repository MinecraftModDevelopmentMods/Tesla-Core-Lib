package net.ndrei.teslacorelib.gui;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.ndrei.teslacorelib.inventory.FilteredFluidTank;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by CF on 2016-12-29.
 */
public class FluidTankPiece extends BasicContainerGuiPiece {
    public static final int WIDTH = 18;
    public static final int HEIGHT = 54;

    private IFluidTank tank;

    public FluidTankPiece(IFluidTank tank, int left, int top) {
        super(left, top, WIDTH, HEIGHT);

        this.tank = tank;
    }

    @Override
    public void drawBackgroundLayer(BasicTeslaGuiContainer container, int guiX, int guiY, float partialTicks, int mouseX, int mouseY) {
        container.bindDefaultTexture();
        container.drawTexturedRect(this.getLeft(), this.getTop(), 1, 189, this.getWidth(), this.getHeight());

        container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 48, 191, this.getWidth() - 4, this.getHeight() - 4);
        if (this.tank != null) {
            FluidStack stack = this.tank.getFluid();
            if ((stack != null) && (stack.amount > 0)) {
                int amount = (stack.amount * (this.getHeight() - 6)) / tank.getCapacity();
                if (stack.getFluid() != null) {
                    Fluid fluid = stack.getFluid();
                    int color = fluid.getColor(stack);
                    ResourceLocation still = fluid.getFlowing(stack); //.getStill(stack);
                    if (still != null) {
                        TextureAtlasSprite sprite = container.mc.getTextureMapBlocks().getTextureExtry(still.toString());
                        if (sprite == null) {
                            sprite = container.mc.getTextureMapBlocks().getMissingSprite();
                        }
                        container.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
                        GlStateManager.enableBlend();
                        container.drawTexturedModalRect(
                                guiX + this.getLeft() + 3,
                                guiY + this.getTop() + 3 + this.getHeight() - 6 - amount,
                                sprite,
                                this.getWidth() - 6, amount);
                        GlStateManager.disableBlend();
                    }
                }
            }
        }
        container.bindDefaultTexture();
        container.drawTexturedRect(this.getLeft() + 2, this.getTop() + 2, 63, 191, this.getWidth() - 4, this.getHeight() - 4);
    }

    @Override
    public void drawForegroundTopLayer(BasicTeslaGuiContainer container, int guiX, int guiY, int mouseX, int mouseY) {
        if (super.isInside(container, mouseX, mouseY) && (this.tank != null)) {
            FluidStack fluid = this.tank.getFluid();
            if (fluid != null) { // && (fluid.amount > 0)) {
                List<String> lines = Lists.newArrayList();
                lines.add(String.format("%sFluid: %s%s", ChatFormatting.DARK_PURPLE, ChatFormatting.LIGHT_PURPLE, fluid.getLocalizedName()));
                lines.add(String.format("%s%,d mb %sof", ChatFormatting.AQUA, this.tank.getFluidAmount(), ChatFormatting.DARK_GRAY));
                lines.add(String.format("%s%,d mb", ChatFormatting.RESET, this.tank.getCapacity()));
                container.drawTooltip(lines, mouseX - guiX, mouseY - guiY);
            }
        }
    }
}
