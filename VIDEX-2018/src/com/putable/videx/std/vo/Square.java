package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.IOEventFilterMouse;
import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.IOEvent;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.interfaces.Stage;


public class Square extends EventAwareVO implements IOEventFilterMouse {

    public Square() {
        super.addFilter(this);
    }
    
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        v2d.getGraphics2D().fillRect(0, 0, 100, 100);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        Pose p = this.getPose();
        p.setPAX(p.getPAX()+2.5f);
        return true;
    }

    @Override
    public boolean matches(IOEvent ioe) {
        return false;
    }

    @Override
    public boolean matches(MouseEventInfo mei) {
        MouseEvent me = mei.getMouseEvent();
        System.out.println("KDKDKD "+me.getModifiers());
        if (me != null && me.getID() == MouseEvent.MOUSE_CLICKED && (me.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            int color = (int) (Math.random() * 0xffffff);
            this.setForeground(new Color(color));
        }
        return false;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }
}
