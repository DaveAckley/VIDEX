package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.IOEventFilterMouse;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.IOEvent;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Stage;

@OIOTop
public class OIOViewport extends EventAwareVO implements IOEventFilterMouse {

    @OIO
    private Rectangle mViewport = new Rectangle(0,0,100,100); //Default to a bit of size for visibility

    @OIO
    private int mBorderSize = 5;
    
    public void setSize(Point2D to) {
        setSize((int) to.getX(), (int) to.getY());
    }

    public void setSize(int width, int height) {
        mViewport.width = width;
        mViewport.height = height;
    }

    public Point2D getSize(Point2D dest) {
        if (dest == null)
            dest = new Point2D.Double();
        dest.setLocation(mViewport.width, mViewport.height);
        return dest;
    }

    public OIOViewport() {
        super.addFilter(this);
    }

    /**
     * Take over drawVO to wrap the clip around drawing the kids
DON'T NEED THIS NOW?
    @Override
    public void drawVO(VOGraphics2D v2d) {
        if (!isEnabled() || !isAlive())
            return;

        v2d.renderVO(this);

        int wid = (int) mSize.getX();
        int hei = (int) mSize.getY();

        Graphics2D g2d = v2d.getGraphics2D();
        Shape old = g2d.getClip();

        g2d.setClip(1, 1, wid - 1, hei - 1);
        for (VO vo : this)
            vo.drawVO(v2d);
        g2d.setClip(old);
    }
?? */
    
    @Override
    public boolean updateThisVO(Stage stage) {
        if (!mViewport.equals(this.getClip()))
            this.setClip(mViewport);
        return true;
    }

    @Override
    public boolean matches(IOEvent ioe) {
        return false;
    }

    @Override
    public boolean matches(MouseEventInfo mei) {
        return false;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // hmm is the clip applied before or after we draw ourselves??
        Graphics2D g2d = v2d.getGraphics2D();
        Rectangle r = new Rectangle(-mBorderSize, -mBorderSize, 
                mViewport.width+2*mBorderSize, 
                mViewport.height+2*mBorderSize);
        g2d.fill(r);

        g2d.setColor(this.getBackground());
        g2d.draw(mViewport);
    }

}
