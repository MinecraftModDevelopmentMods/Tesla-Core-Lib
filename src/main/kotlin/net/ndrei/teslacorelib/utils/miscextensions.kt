package net.ndrei.teslacorelib.utils

import java.awt.Color

/**
 * Created by CF on 2017-07-15.
 */
fun Color.withAlpha(alpha: Float) = this.withAlpha((alpha * 255.0f + 0.5f).toInt())
fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
