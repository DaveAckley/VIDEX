package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics2D;

public class StandardHyperspaceRenderer implements HyperspaceRenderer {
	private Graphics2D mG2d;
	private double mYScale;
	private double mYOrigin;
	private boolean mInHitmap;
	
	public Graphics2D getG2d() {
		return mG2d;
	}

	public void setG2d(Graphics2D mG2d) {
		this.mG2d = (Graphics2D) mG2d.create();
	}

	@Override
	public double getYScale() {
		return mYScale;
	}

	public void setYScale(double mYScale) {
		this.mYScale = mYScale;
	}

	@Override
	public double getYOrigin() {
		return mYOrigin;
	}

	public void setYOrigin(double mYOrigin) {
		this.mYOrigin = mYOrigin;
	}

	@Override
	public Graphics2D getGraphics2D() {
		return mG2d;
	}

	public boolean isInHitmap() {
		return mInHitmap;
	}

	public void setIsInHitmap(boolean mInHitmap) {
		this.mInHitmap = mInHitmap;
	}

	@Override
	public Color getFg(FO fo) {
		if (this.mInHitmap) return new Color(fo.getIndex(),false);
		return fo.getDisplayedBody().getFg();
	}

	@Override
	public Color getBg(FO fo) {
		if (this.mInHitmap) return new Color(fo.getIndex(),false);
		return fo.getDisplayedBody().getBg();
	}

}
