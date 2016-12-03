package net.ndrei.teslacorelib.capabilities.hud;

import java.util.List;

/**
 * Created by CF on 2016-12-03.
 */
@SuppressWarnings("unused") // api interface
public interface IHudInfoProvider {
    List<HudInfoLine> getHUDLines();
}
