package net.ndrei.teslacorelib.gui;

import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.List;

/**
 * Created by CF on 2016-12-21.
 */
public interface IGuiContainerPiecesProvider {
    List<IGuiContainerPiece> getGuiContainerPieces(BasicTeslaGuiContainer container);
}
