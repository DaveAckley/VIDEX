package com.putable.hyperspace.core;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class BitVectorH extends AFO implements Iterable<BitBox> {
	private double mAltScale = .66; //< Size of alt relative to hero
	private double mYScale = 1;
	private double mYOrigin = 0;
	private BitBox [] mBits = new BitBox[0];
	private BitBox mHero;
	public BitVectorH(Finite2DSpace f2) {
		super(f2);
		mHero = new BitBox(f2);
	}
	public BitVectorH(Finite2DSpace f2, int size) {
		this(f2);
		resize(size);
	}
	
	public void evaluate(Function func) {
		double score = func.evaluate(this);
		this.setY((int) score);
		for (BitBox bb : mBits) {
			BitValue cur = bb.getValue();
			bb.setValue(cur.opposite());
			double altscore= func.evaluate(this);
			bb.setValue(cur);
			bb.getAlternative().setY((int) altscore);
		}
	}
	@Override 
	public void setX(int x) {
		if (x != this.getX()) {
			super.setX(x);
			reconfigure();
		}
	}
	public double getYScale() {
		return mYScale;
	}
	public void setYScale(double mYScale) {
		this.mYScale = mYScale;
	}
	public double getYOrigin() {
		return mYOrigin;
	}
	public void setYOrigin(double mYOrigin) {
		this.mYOrigin = mYOrigin;
	}
	@Override 
	public void setY(int y) {
		if (y != this.getY()) {
			super.setY(y);
			reconfigure();
		}
	}
	public void setBitBoxSize(int size) {
		if (size != mHero.getSize()) {
			mHero.setSize(size);
			reconfigure();
		}
	}
	
	public void setBitBoxBorder(int bsize) {
		if (bsize != mHero.getBorder()) {
			mHero.setBorder(bsize);
			reconfigure();
		}
	}
	
	public void reconfigure() {
		int size = mHero.getSize();
		for (int i = 0; i < mBits.length; ++i) {
			mBits[i].setSize(size);
			mBits[i].setX(this.getX()+i*size);
			BitBox alt = mBits[i].getAlternative(); 
			if (alt != null) {
				int altsize = (int) (size*this.mAltScale);
				alt.setSize(altsize);
				alt.setX(this.getX()+i*size);
			}
		}
	}
	
	public void resizeOLD(int newsize) {
		this.setY((int) this.getSpace().getBounds().getCenterY());
		int width = newsize*1_000_000;
		this.setX((int) this.getSpace().getBounds().getCenterX()); // XXX NEEDS OFFSET 
		BitBox[] oldBits = mBits;
		mBits = new BitBox[newsize];
		int size = mHero.getSize();
		for (int i = 0; i < mBits.length; ++i) {
			if (oldBits != null && i < oldBits.length) { 
				mBits[i] = oldBits[i];
			} else {
				mBits[i] = new BitBox(this.getSpace());
				mBits[i].copy(mHero, false);
				mBits[i].setPosition(this.getX()+i*size, this.getY());
				mBits[i].setAlternative(new BitBox(this.getSpace()));
			}
		}
	}

	
	public void resize(int newsize) {
		BitBox[] oldBits = mBits;
		mBits = new BitBox[newsize];
		for (int i = 0; i < mBits.length; ++i) {
			if (oldBits != null && i < oldBits.length) { 
				mBits[i] = oldBits[i];
			} else {
				mBits[i] = new BitBox(this.getSpace());
				mBits[i].copy(mHero, false);
				mBits[i].setAlternative(new BitBox(this.getSpace()));
			}
		}
		reconfigure();
	}

	@Override
	public Color getForegroundColor() {
		return BitValue.BIT_INVISIBLE.getFill();
	}

	@Override
	public Color getBackgroundColor() {
		return BitValue.BIT_INVISIBLE.getFill();
	}

	@Override
	public void draw(HyperspaceRenderer hr) {
		for (BitBox bb : mBits) {
			bb.draw(hr);
		}
	}
	@Override
	public Iterator<BitBox> iterator() {
		return Arrays.asList(this.mBits).iterator();
	}
	

}
