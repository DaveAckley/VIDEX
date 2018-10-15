package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public class Label extends EventAwareVO {

    @OIO
    private String mLabel  = "";

    public void setLabel(String str) {
        if (str == null) throw new IllegalArgumentException();
        mLabel = str;
    }
    public String getLabel() {
        return mLabel;
    }
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        FontMetrics fm = getUnscrewedFontMetrics(g2d, 0);
        int height = fm.getAscent() + fm.getDescent();
        g2d.clearRect(0, -fm.getDescent(), fm.stringWidth(mLabel ), height);
        g2d.drawString(mLabel , 0, fm.getAscent()-fm.getDescent());
    }

    public Label() { this(0,0); }

    public Label(int x, int y) {
        this.setBackground(Color.BLUE);
        this.setForeground(Color.WHITE);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return true;
    }
}
