package net.ndrei.teslacorelib.capabilities.hud;

import java.awt.*;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings({"unused", "WeakerAccess"}) // api class
public class HudInfoLine {
    public String text = null;
    public Color color = null;
    public Color background = null;
    public Color border = null;

    public float percent = 0.0f;
    public Color percentColor = null;

    public TextAlignment alignment = TextAlignment.LEFT;

    public HudInfoLine(String text) {
        this(null, text);
    }

    public HudInfoLine(Color color, String text) {
        this(color, null, text);
    }

    public HudInfoLine(Color color, Color background, String text) {
        this(color, background, null, text);
    }

    public HudInfoLine(Color color, Color background, Color border, String text) {
        this.text = text;
        this.color = color;
        this.background = background;
        this.border = border;
    }

    public HudInfoLine setTextAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public HudInfoLine setProgress(float percent, Color percentColor) {
        this.percent = percent;
        this.percentColor = percentColor;
        return this;
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
}
