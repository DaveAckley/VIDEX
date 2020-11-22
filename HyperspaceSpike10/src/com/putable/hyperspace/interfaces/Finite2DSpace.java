package com.putable.hyperspace.interfaces;

import java.awt.geom.Rectangle2D;

import com.putable.hyperspace.core.FO;
import com.putable.hyperspace.core.HyperspaceRenderer;

public interface Finite2DSpace {
	public Rectangle2D getBounds() ;
	public FO getRoot() ;
	public int getIndexForFO(FO fo) ;
	public FO getFOForIndex(int index) ;
	public void draw(HyperspaceRenderer hr ) ;
}
