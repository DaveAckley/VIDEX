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

    public static final int HALIGN_LEFT = 0;
    public static final int HALIGN_CENTER = 1;
    public static final int HALIGN_RIGHT = 2;

    public static final int VALIGN_BOTTOM = 3;
    public static final int VALIGN_MIDDLE = 4;
    public static final int VALIGN_TOP = 5;
    
    @OIO
    private int mHorizontalAlign = HALIGN_LEFT;
    
    @OIO
    private int mVerticalAlign = VALIGN_BOTTOM;

    public void setHorizontalAlign(int ha) {
        if (ha < HALIGN_LEFT || ha > HALIGN_RIGHT)
            throw new IllegalArgumentException();
        mHorizontalAlign = ha;
    }

    public void setVerticalAlign(int va) {
        if (va < VALIGN_BOTTOM || va > VALIGN_TOP)
            throw new IllegalArgumentException();
        mVerticalAlign = va;
    }
    
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
        int width = fm.stringWidth(mLabel);
        int x=0,y=0;
        switch (mHorizontalAlign) {
        case HALIGN_LEFT:   x = 0; break;
        case HALIGN_CENTER: x = -width/2; break;
        case HALIGN_RIGHT:  x = -width; break;
        }
        switch (mVerticalAlign) {
        case VALIGN_BOTTOM: y = 0; break;
        case VALIGN_MIDDLE:  y = -height/2; break;
        case VALIGN_TOP:     y = -height; break;
        }
        g2d.clearRect(x, y-fm.getDescent(), width, height);
        g2d.drawString(mLabel , x, y+fm.getAscent()-fm.getDescent());
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
