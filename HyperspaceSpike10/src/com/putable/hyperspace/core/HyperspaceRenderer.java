package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics2D;

public interface HyperspaceRenderer {
	public Graphics2D getGraphics2D() ;
	public Color getFg(FO fo) ;
	public Color getBg(FO fo) ;
	public double getYScale() ;
	public double getYOrigin() ;
}
