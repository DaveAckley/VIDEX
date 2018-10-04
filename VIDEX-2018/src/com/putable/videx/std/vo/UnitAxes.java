package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.TriggerPoint;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class UnitAxes extends EventAwareVO {
    private int mMaxX = 100;
    private int mMaxY = 100;

    @Override
    public boolean handleOverlappedTriggerPoint(TriggerPoint tp, VO other) {
        System.out.println("TRIGGERED " + tp + " BY " + other);
        return false;
    }
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        Font font = new Font("Inconsolata", Font.PLAIN, 18);
        g2d.setFont(font);
        g2d.clearRect(0, 0, mMaxX, mMaxY);
        g2d.drawString(((int) (10.0*this.getPose().getR()*180/Math.PI))/10.0+"", mMaxX/2, mMaxY/2);
        g2d.drawString(this.getPose().getOAX()+", "+this.getPose().getOAY(), mMaxX/2, mMaxY/4);
        g2d.drawString("(0,0)", 0, 0);
        g2d.drawString("("+mMaxY+",0)", mMaxX, 0);
        g2d.drawString("(0,"+mMaxY+")", 0, mMaxY);
        g2d.drawString("OOF", mMaxX/3, mMaxY/3);    
    }

    public UnitAxes() { this(0,0,0,0); }

    public UnitAxes(int x, int y, float scale, float rot) {
        this.setBackground(Color.BLUE);
        this.setForeground(Color.YELLOW);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
        this.getPose().setOAX(mMaxX/2);
        this.getPose().setOAY(mMaxY/2);
        this.getPose().setSX(scale);
        this.getPose().setSY(scale);
        this.getPose().setR(rot);
        /*
        this.addPendingTriggerPoint(0, 0);
        this.addPendingTriggerPoint(0, 99);
        this.addPendingTriggerPoint(99, 0);
        this.addPendingTriggerPoint(99, 99);
         */
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

}
