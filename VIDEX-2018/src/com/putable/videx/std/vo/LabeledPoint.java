package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;

public class LabeledPoint extends EventAwareVO {
    private int mPointRadius = 2;

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        int px = (int) this.getPose().getPAX(); 
        int py = (int) this.getPose().getPAY(); 

        g2d.fillArc(-mPointRadius, -mPointRadius, 2*mPointRadius,2*mPointRadius,0,360);
        g2d.drawString("("+px+","+py+")", 0, 0);
    }

    public LabeledPoint() { this(0,0); }

    public LabeledPoint(int x, int y) {
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }
}
