package net.ndrei.teslacorelib.render

import java.awt.Color

/**
 * Created by CF on 2017-06-28.
 */
class HudInfoLine(val color: Color?, val background: Color?, val border: Color?, val text: String) {
    var percent = 0.0f
    var percentColor: Color? = null

    var alignment = TextAlignment.LEFT

    constructor(text: String) : this(null, null, null, text)
    constructor(color: Color, text: String) : this(color, null, null, text)
    constructor(color: Color, background: Color, text: String) : this(color, background, null, text)

    fun setTextAlignment(alignment: TextAlignment): HudInfoLine {
        this.alignment = alignment
        return this
    }

    fun setProgress(percent: Float, percentColor: Color): HudInfoLine {
        this.percent = percent
        this.percentColor = percentColor
        return this
    }

    enum class TextAlignment {
        LEFT, CENTER, RIGHT
    }
}
