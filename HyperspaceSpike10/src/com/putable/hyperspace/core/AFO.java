package com.putable.hyperspace.core;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public abstract class AFO implements FO {
	private final Finite2DSpace mSpace;
	public Finite2DSpace getSpace() { return mSpace; }
	
	private int mX;
	private int mY;
	private int mWidth;
	private int mHeight;
	private int mIndex;
	private Color mRealBg;
	private Color mRealFg;
	private FO mParent = null;

	public void setParent(FO parent) {
		if ((this.getParent() == null) == (parent == null))
			throw new IllegalArgumentException();
		mParent = parent;
	}
	
	@Override
	public FO getParent() { return mParent; }
	
	public AFO(Finite2DSpace space) {
		if (space == null) throw new NullPointerException();
		this.mSpace = space;
		this.mIndex = mSpace.getIndexForFO(this);
	}
	
	@Override
	public int getIndex() { return mIndex; }
	
	@Override
	public Color getForegroundColor() { return mRealFg; }
	@Override
	public Color getBackgroundColor() { return mRealBg; }
	
	public void setForegroundColor(Color col) {
		mRealFg = col;
	}

	public void setBackgroundColor(Color col) {
		mRealBg = col;
	}

	public int getHitmapIndex() { 
		return mIndex; 
	}
	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	@Override
	public int getX() {
		return mX;
	}
	@Override
	public int getY() {
		return mY;
	}
	@Override
	public int getWidth() {
		return mWidth;
	}
	@Override
	public int getHeight() {
		return mHeight;
	}

	public void setX(int mX) {
		this.mX = mX;
	}
	public void setY(int mY) {
		this.mY = mY;
	}
	public void setWidth(int mWidth) {
		this.mWidth = mWidth;
	}
	public void setHeight(int mHeight) {
		this.mHeight = mHeight;
	}
	
}
