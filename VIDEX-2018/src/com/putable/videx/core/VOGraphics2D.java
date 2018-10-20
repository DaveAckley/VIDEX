package com.putable.videx.core;

import java.awt.Graphics2D;
import java.awt.Shape;

import com.putable.videx.interfaces.VO;

public interface VOGraphics2D {

    public abstract void startStageRender(Graphics2D g2d);

    /**
     * Start rendering vo.
     * 
     * @param vo
     * @return the clip shape that was previously in use for rendering
     */
    public abstract Shape startVORender(VO vo);

    public abstract void renderVO(VO vo);

    /**
     * Finish vo rendering
     * 
     * @param shape
     *            the clip shape returned from the associated
     *            {@link #startVORender(VO)}
     */
    public abstract void finishVORender(Shape shape);

    public abstract void finishStageRender();

    public abstract Graphics2D getGraphics2D();

    boolean isRenderingToHitmap();

}