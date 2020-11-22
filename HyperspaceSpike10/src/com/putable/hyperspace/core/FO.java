package com.putable.hyperspace.core;

import java.util.Iterator;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.FOEventHandler;
import com.putable.hyperspace.interfaces.Finite2DSpace;

public interface FO extends FOEventHandler {
	
	public void transform(HIP hip) ; //< Recompute your displaybody
	public void step(HIP hip); //< Update yourself 
	public void draw(HyperspaceRenderer hr); //< Draw yourself
	
	Iterator<FO> kids(); //< Return a visitor for your kids

	public Body getCurBody(); //< access ground truth body
	public Body getDisplayedBody(); //< access tweened display body

	public boolean isActive(); //< Should be updated
	public boolean isVisible(); //< Should be drawn
	//public void refresh();
	public int getIndex();
	public FO getParent();
	public Finite2DSpace getSpace();

	public default void draw(HIP hip) {      //< Update yourself 
		draw(hip.mSHR);
	}

}
