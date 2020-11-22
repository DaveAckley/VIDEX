package com.putable.hyperspace.core;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.Finite2DSpace;

/**
 * A box of given size centered on (X,Y)
 * @author ackley
 *
 */
public class BitBox extends AFO {

/*
	@Override
	public void stepKids(HIP hip) {
		if (mAlternative != null) mAlternative.step(hip);
	}
*/
	private double mBorder;
	private BitBox mAlternative = null;
	public Point2D getCenter(Point2D p) {
		if (p == null) p = new Point2D.Double();
		double half = this.getSize()/2;
		double finaly = this.getMinY()+half;
		p.setLocation(this.getMinX() + half, 
				finaly);
		return p;
	}

	private BitValue mBitValue = BitValue.BIT_FIXED_OFF;

	public BitBox getAlternative() { return mAlternative; }
	public void setAlternative(BitBox alt) {
		if (alt != null) {
			if (alt == this || alt.getAlternative() != null)
				throw new IllegalStateException();
		}
		mAlternative = alt;
		alt.mAlternative = null;
		alt.setParent(this);
	}
	public boolean getAltActive() {
		BitBox theAlt = this;
		if (this.mAlternative != null) theAlt = this.mAlternative;
		if (theAlt != null) {
			return theAlt.isActive();
		}
		return false;
	}
	public void setAltActive(boolean active) {
		BitBox theAlt = this;
		if (this.mAlternative != null) theAlt = this.mAlternative;
		if (theAlt != null) {
			theAlt.setIsActive(active);
		}
	}
	public BitValue getValue() { return mBitValue; }
	public void setValue(BitValue bv) { 
		if (bv == null) throw new NullPointerException();
		if (mBitValue != bv) {
			mBitValue = bv;
			this.setCurBackgroundColor(mBitValue.getBorder());
			this.setCurForegroundColor(mBitValue.getFill());
			this.retween();

			if (mAlternative != null)
				mAlternative.setValue(bv.opposite());
		}
	}
	public void setSize(double size) {
		this.setCurWidth(size);
		this.setCurHeight(size);
	}
	public double getSize() { return this.getCurWidth(); }
	public double getBorder() { return mBorder; }
	public void setBorder(double bsize) {
		mBorder = bsize;
	}

	public void copy(BitBox other, boolean copyValue) {
		if (copyValue) mBitValue = other.mBitValue;
		this.getCurBody().copy(other.getCurBody());
		mBorder = other.mBorder;
	}
	public BitBox(Finite2DSpace f2,double side, double border) {
		super(f2);
		this.setCurX(0);
		this.setCurY(0);
		this.setSize(side);
		this.setBorder(border);
	}

	
	@Override
	public void draw(HyperspaceRenderer hr) {
		if (!this.isActive()) return;
		Graphics2D g2d = hr.getGraphics2D();
		Body usb = this.getDisplayedBody();
		Rectangle2D bds = usb.getBounds2D();
		
		Shape clear= new Rectangle2D.Double(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
		Shape fill = new Rectangle2D.Double(bds.getX()+mBorder/2, bds.getY()+mBorder/2, 
				bds.getWidth()-mBorder,  bds.getHeight()-mBorder);
//		Shape fill = clear;


		BitBox theAlt = mAlternative;
		if (theAlt != null && !theAlt.isActive()) theAlt = null;
		if (theAlt != null) {
			Body altb = theAlt.getDisplayedBody();
			g2d.setPaint(hr.getBg(this));
			Point2D us = usb.getCenter(null);
			Point2D them = altb.getCenter(null);
			Shape line = new Line2D.Double(us,them);
			Stroke old = g2d.getStroke();
			g2d.setStroke(new BasicStroke((float) mBorder/2));
			g2d.draw(line);
			g2d.setStroke(old);
		}
		g2d.setPaint(hr.getBg(this));
		g2d.fill(clear);
		g2d.setPaint(hr.getFg(this));
		g2d.fill(fill);
		if (theAlt!= null)
			theAlt.draw(hr);
	}
	
	private double getMinX() {
		return getCurX()-getSize()/2;
	}
	private double getMinY() {
		return getCurY()-getSize()/2;
	}

	@Override
	public boolean handle(FOEvent foe) {
		if (foe instanceof FOEventKey) {
			FOEventKey foek = (FOEventKey) foe;
			KeyEvent ke = foek.mKeyEvent;
			if (ke.getID()==KeyEvent.KEY_TYPED) {
				char ch = ke.getKeyChar();
				if (ch == 'a') {
					this.setAltActive(!this.getAltActive());
					return true;
				}
			}
		}
		if (foe instanceof FOEventClick) {
			//FOEventClick foec = (FOEventClick) foe;
			if (this.mAlternative != null) 
				return this.mAlternative.handle(foe);
			// Otherwise I am the alternative
			FO par = this.getParent();
			if (!(par instanceof BitBox)) { 
				System.out.println("NO BB PAR? "+par);
				return false;
			}
			BitBox bb = (BitBox) par;
			bb.setValue(this.getValue());
			System.out.println("FLIPT TO MEEEE "+this+" par "+this.getParent());
			return true;
		}
		return false;
	}
	
	@Override
	public Iterator<FO> kids() {
		return new Iterator<FO>() {
			BitBox mAlt = mAlternative;
			@Override
			public boolean hasNext() {
				return mAlt != null;
			}

			@Override
			public FO next() {
				if (mAlt == null) throw new ArrayIndexOutOfBoundsException();
				FO ret = mAlt;
				mAlt = null;
				return ret;
			}
			
		};
	}

}
