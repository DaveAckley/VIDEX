package com.putable.videx.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.putable.videx.interfaces.VO;

/**
 * A {@link VOGraphics2D} renderer that computes a hitmap for input processing
 * 
 * @author ackley
 * 
 */
public class StageGraphics2D implements VOGraphics2D {
    private Graphics2D mScreenG2D;
    private boolean mShowHitmap = false;
    private final Hitmap mHitmap;
    
    private boolean mIsRenderingToHitmap; // For clients that truly need to know..

    @Override
    public boolean isRenderingToHitmap() { return mIsRenderingToHitmap; }
    
    public Hitmap getHitmap() { return mHitmap; }
    public SXRandom getRandom() { return mHitmap.getRandom(); }
    
    public StageGraphics2D(Hitmap hm) {
        if (hm == null) throw new NullPointerException();
        this.mHitmap = hm;
    }
    
    public void resizeHitmap(int width, int height) {
        mHitmap.resizeHitmap(width, height);
    }

    @Override
    public void startStageRender(Graphics2D g2d) {
        mScreenG2D = g2d;
        mHitmap.startDrawing();
    }

    @Override
    public void renderVO(VO vo) {
        if (mScreenG2D != null)
            renderVOTo(vo, mScreenG2D, vo.getForeground(), vo.getBackground(), false);
        if (vo.isMouseAware()) {
            mHitmap.initGraphicsForVO(vo);
            renderVOTo(vo, mHitmap.getGraphics2D(), null, null, true);
        }
    }

    private void renderVOTo(VO vo, Graphics2D g2d, Color fg, Color bg, boolean toHitmap) {
        this.mIsRenderingToHitmap = toHitmap;
        Color oldfg = g2d.getColor();
        Color oldbg = g2d.getBackground();
        AffineTransform at = g2d.getTransform();

        g2d.setTransform(vo.getVOCToPixelTransform(null));
        if (fg != null)
            g2d.setColor(fg);
        if (bg != null)
            g2d.setBackground(bg);

        vo.drawThisVO(this);

        g2d.setColor(oldfg);
        g2d.setBackground(oldbg);
        g2d.setTransform(at);
    }

    @Override
    public void finishStageRender() {
        mHitmap.finishDrawing();
        if (mShowHitmap) {
            mHitmap.drawHitmap(mScreenG2D);
        }
    }

    /**
     * Draw the hitmap on top of the rendered graphics (for debugging), if
     * enable
     * 
     * @param enable
     */
    public void setHitmapDisplay(boolean enable) {
        mShowHitmap = enable;
    }

    @Override
    public Graphics2D getGraphics2D() {
        if (mIsRenderingToHitmap)
            return mHitmap.getGraphics2D();
        return this.mScreenG2D;
    }

    @Override
    public Color getWorkingColor(Color color, VO forVO) {
        if (mIsRenderingToHitmap)
            return forVO.getForeground();
        return color;
    }
}
