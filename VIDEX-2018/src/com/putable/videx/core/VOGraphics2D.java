package com.putable.videx.core;

import java.awt.Color;
import java.awt.Graphics2D;

import com.putable.videx.interfaces.VO;

public interface VOGraphics2D {
  
    public abstract void startStageRender(Graphics2D g2d);

    public abstract Color getWorkingColor(Color color, VO forvo);

    public abstract void renderVO(VO vo);

    public abstract void finishStageRender();

    public abstract Graphics2D getGraphics2D();

    boolean isRenderingToHitmap();

}