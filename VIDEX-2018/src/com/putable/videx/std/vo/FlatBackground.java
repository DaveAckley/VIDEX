package com.putable.videx.std.vo;

import java.awt.Color;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;

public class FlatBackground extends EventAwareVO {

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
/*
        Total combined monitors size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
*/
/*
 *      Just current monitor minus taskbar size (even though we're full screen..)
        Rectangle2D rect =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
*/
        v2d.getGraphics2D().clearRect(0, 0, 10000, 10000);
    }

    public FlatBackground() {
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }
}
