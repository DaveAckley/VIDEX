package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.FOEventHandler;
import com.putable.hyperspace.interfaces.Finite2DSpace;

public class Collector extends AFO implements Iterable<FO> {
	private List<FOEventHandler> mHandlers = new LinkedList<FOEventHandler>();
	public void addFOEventHandler(FOEventHandler foe) {
		mHandlers.add(foe); // Add at end
	}
	public boolean removeFOEventHandler(FOEventHandler foe) {
		for (Iterator<FOEventHandler> itr = mHandlers.iterator(); itr.hasNext(); ) {
			FOEventHandler lfoe = itr.next();
			if (lfoe == foe) {
				itr.remove();  // Remove frontmost match 
				return true;
			}
		}
		return false;
	}

	public Collector(Finite2DSpace space) {
		super(space);
		this.setCurBackgroundColor(Color.black);
		this.setCurForegroundColor(Color.black);
	}

	private List<FO> mKids = new LinkedList<FO>();

	@Override
	public Iterator<FO> kids() {
		return mKids.iterator();
	}
	
	public void addChild(AFO fo) {
		if (fo.getParent() != null) throw new IllegalArgumentException();
		mKids.add(fo);
		fo.setParent(this);
	}
	
	public Iterator<FO> getChildren() {
		return mKids.iterator();
	}
/*	
	@Override 
	public void step(HIP hip) {
		this.stepKids(hip);
	}
	

	@Override 
	public void stepKids(HIP hip) {
		for (FO kid : this) {
			kid.step(hip);
		}
	}
*/	
	@Override
	public void draw(HyperspaceRenderer hr) {
		Graphics2D g2d = hr.getGraphics2D();
		g2d.setColor(hr.getBg(this));
		// Collector has to paint the world or else it won't get hits
		Rectangle2D rect = new Rectangle2D.Double(-1_000_000, -1_000_000, 10_000_000, 10_000_000);
		g2d.fill(rect);

		Body body = this.getDisplayedBody();
		AffineTransform old = g2d.getTransform();
		g2d.translate(body.getX(), body.getY());
		for (FO kid : this) {
			kid.draw(hr);
		}
		g2d.setTransform(old);
	}

	@Override
	public Iterator<FO> iterator() {
		return this.mKids.iterator();
	}

	@Override
	public boolean handle(FOEvent foe) {
		for (FOEventHandler foeh : mHandlers) 
			if (foeh.handle(foe)) return true;
		return false;
	}

}
