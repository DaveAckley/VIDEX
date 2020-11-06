package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.putable.hyperspace.interfaces.Finite2DSpace;

/**
 * A box of given size centered on (X,Y)
 * @author ackley
 *
 */
public class BitBox extends AFO {
	private int mBorder = 40_000;
	private BitBox mAlternative = null;
	public Point2D getCenter(Point2D p) {
		if (p == null) p = new Point2D.Double();
		double half = this.getSize()/2;
		double finaly = this.getMinY()+half;
		p.setLocation(this.getMinX() + half, 
				finaly);
		return p;
	}
/*
	public int getOffsetAlternative() {
		return mOffsetAlternative;
	}
	public void setOffsetAlternative(int mOffsetAlternative) {
		if (mAlternative == null) throw new IllegalStateException();
		this.mAlternative.mOffsetAlternative = mOffsetAlternative;
	}
*/
	private BitValue mBitValue = BitValue.BIT_FIXED_OFF;

	public BitBox getAlternative() { return mAlternative; }
	public void setAlternative(BitBox alt) {
		if (alt != null) {
			if (alt == this || alt.getAlternative() != null)
				throw new IllegalStateException();
		}
		mAlternative = alt;
		alt.mAlternative = null;
	}
	public BitValue getValue() { return mBitValue; }
	public void setValue(BitValue bv) { 
		if (bv == null) throw new NullPointerException();
		mBitValue = bv;
		if (mAlternative != null)
			mAlternative.setValue(bv.opposite());
	}
	public void setSize(int size) {
		this.setWidth(size);
		this.setHeight(size);
	}
	public int getSize() { return this.getWidth(); }
	public int getBorder() { return mBorder; }
	public void copy(BitBox other, boolean copyValue) {
		if (copyValue) mBitValue = other.mBitValue;
		this.setSize(other.getSize());
		mBorder = other.mBorder;
		this.setX(other.getX());
		this.setY(other.getY());
	}
	public void setBorder(int bsize) {
		mBorder = bsize;
	}
	public BitBox(Finite2DSpace f2) {
		super(f2);
		this.setX(8_000_000);
		this.setY(4_500_000);
		this.setSize(1_000_000);
	}

	@Override
	public Color getForegroundColor() {
		return mBitValue.getFill();
	}

	@Override
	public Color getBackgroundColor() {
		return mBitValue.getBorder();
	}
	
	@Override
	public void draw(HyperspaceRenderer hr) {
		Graphics2D g2d = hr.getGraphics2D();
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);
		
		Shape clear= new Rectangle2D.Double(getMinX(), getMinY(), getWidth(), getHeight());
		Shape fill = new Rectangle2D.Double(getMinX()+mBorder/2, getMinY()+mBorder/2, 
				getWidth()-mBorder,  getHeight()-mBorder);
//		Shape fill = clear;


		if (mAlternative != null) {
			g2d.setPaint(hr.getBg(this));
			Point2D us = this.getCenter(null);
			Point2D them = mAlternative.getCenter(null);
			Shape line = new Line2D.Double(us,them);
			g2d.draw(line);
		}
		g2d.setPaint(hr.getBg(this));
		g2d.fill(clear);
		g2d.setPaint(hr.getFg(this));
		g2d.fill(fill);
		if (mAlternative != null)
			mAlternative.draw(hr);

	}
	
	private int getMinX() {
		return getX()-getSize()/2;
	}
	private int getMinY() {
		return getY()-getSize()/2;
	}

}
