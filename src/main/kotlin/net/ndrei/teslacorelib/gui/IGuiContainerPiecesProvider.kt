package net.ndrei.teslacorelib.gui

/**
 * Created by CF on 2017-06-28.
 */
interface IGuiContainerPiecesProvider {
    fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>): MutableList<IGuiContainerPiece>
}