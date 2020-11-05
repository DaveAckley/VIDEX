package com.putable.hyperspace.core;

import java.awt.Color;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public interface FO {
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public Color getForegroundColor(); //< The 'real' fg
	public Color getBackgroundColor(); //< The 'real' bg
	public void draw(HyperspaceRenderer hr);
	public int getIndex();
	public FO getParent();
	public Finite2DSpace getSpace();
}
