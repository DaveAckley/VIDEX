package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.HittableImage;
import com.putable.videx.core.IOEventFilterMouse;
import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.IOEvent;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.interfaces.Stage;


public class Image extends EventAwareVO implements IOEventFilterMouse {

    private final HittableImage mImage;
     
    public void setImageFromPathIfPossible(String path) {
        mImage.setImageFromPathIfPossible(path);
    }

    public Image() {
        mImage = new HittableImage(this);
    }
    
    public Image(String path) {
        this();
        super.addFilter(this);
        setImageFromPathIfPossible(path);
    }
    
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        BufferedImage bi = mImage.getImage(v2d);
        v2d.getGraphics2D().drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        Pose p = this.getPose();
        p.setR(p.getR()+0.01f);
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
