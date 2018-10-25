package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class TimedNotification extends Label {
    
    public static TimedNotification postOn(VO vo, String text) {
        TimedNotification tn = new TimedNotification(text); 
        vo.addPendingChild(tn);
        return tn;
    }
    
    @OIO
    private int mPostingTime = 50;
    @OIO
    private int mUpdatesSincePosting = 0;
    
    public TimedNotification() { this(0,0); }

    public TimedNotification(String text) {
        this();
        this.setLabel(text);
    }
    
    public TimedNotification(int x, int y) {
        this.setBackground(Color.YELLOW);
        this.setForeground(Color.BLACK);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        if (++this.mUpdatesSincePosting >= this.mPostingTime && !isMouseTarget())
            this.killVO();
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return true;
    }

}
