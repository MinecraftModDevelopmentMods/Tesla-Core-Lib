package net.ndrei.teslacorelib.inventory

/**
 * Created by CF on 2017-06-28.
 */
class BoundingRectangle(val left: Int, val top: Int, val width: Int, val height: Int) {
    val right: Int
        get() = this.left + this.width
    val bottom: Int
        get() = this.top + this.height

    val isEmpty
        get() = (this === EMPTY)

    companion object {
        val EMPTY = BoundingRectangle(0, 0, 0, 0)
    }
}
