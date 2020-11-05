package com.putable.hyperspace.core;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class Collector extends AFO implements Iterable<FO> {
	public Collector(Finite2DSpace space) {
		super(space);
	}

	private List<FO> mKids = new LinkedList<FO>();

	public void addChild(AFO fo) {
		if (fo.getParent() != null) throw new IllegalArgumentException();
		mKids.add(fo);
		fo.setParent(this);
	}
	
	public Iterator<FO> getChildren() {
		return mKids.iterator();
	}
	

	@Override
	public void draw(HyperspaceRenderer hr) {
		Graphics2D g2d = hr.getGraphics2D();
		AffineTransform old = g2d.getTransform();
		g2d.translate(this.getX(), this.getY());
		for (FO kid : this) {
			kid.draw(hr);
		}
		g2d.setTransform(old);
	}

	@Override
	public Iterator<FO> iterator() {
		return this.mKids.iterator();
	}

}
