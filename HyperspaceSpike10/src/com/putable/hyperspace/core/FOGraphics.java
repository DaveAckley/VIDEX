package com.putable.hyperspace.core;

import java.awt.Graphics2D;
import java.awt.Shape;


public interface FOGraphics {
    public abstract void startStageRender(Graphics2D g2d);

    /**
     * Start rendering FO.
     * 
     * @param FO
     * @return the clip shape that was previously in use for rendering
     */
    public abstract Shape startFORender(FO FO);

    public abstract void renderFO(FO FO);

    /**
     * Get the Stage that we are on
     * @return
     */
    //public abstract Stage getStage();
    
    /**
     * Finish FO rendering
     * 
     * @param shape
     *            the clip shape returned from the associated
     *            {@link #startFORender(FO)}
     */
    public abstract void finishFORender(Shape shape);

    public abstract void finishStageRender();

    public abstract Graphics2D getGraphics2D();

    boolean isRenderingToHitmap();

}
