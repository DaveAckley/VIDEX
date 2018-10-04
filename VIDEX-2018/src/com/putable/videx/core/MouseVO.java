package com.putable.videx.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class MouseVO extends StandardVO implements VO {
    private Point2D mMousePoint = new Point2D.Float();
    public static final int UPDATES_UNTIL_HIDING = 200;
    private int mHideTimer = UPDATES_UNTIL_HIDING;

    public MouseVO() {
        this.setBackground(Color.DARK_GRAY);
        this.setForeground(Color.WHITE);
    }
    
    private void setMousePoint(Point2D from) {
        if (mMousePoint.getX() != from.getX() ||
            mMousePoint.getY() != from.getY()) {
            mMousePoint.setLocation(from);
            mHideTimer = UPDATES_UNTIL_HIDING;
        }
    }
    
    public void track(Point2D mousePos) {
        if (mousePos != null)
            setMousePoint(mousePos);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        if (mHideTimer >= 0) --mHideTimer;
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        if (v2d.isRenderingToHitmap()) return;
        if (mHideTimer < 0) return;
        Graphics2D g2d = v2d.getGraphics2D();
        final int MOUSE_RADIUS = 10;
        g2d.setColor(Color.red);
        g2d.drawRect((int) (mMousePoint.getX()-MOUSE_RADIUS), 
                (int) (mMousePoint.getY()-MOUSE_RADIUS),
                2*MOUSE_RADIUS,2*MOUSE_RADIUS);
    }

    @Override
    public boolean isMouseAware() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFocusAware() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mouseEntered(Point2D at) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragAtStage(Point2D at, int state) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean zoomAround(Point2D at, double amount) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rotateAround(Point2D at, double amount) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleMouseEvent(MouseEventInfo mei) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean handleSpecialEvent(SpecialEventInfo mei) {
        // TODO Auto-generated method stub
        return false;
    }

}
