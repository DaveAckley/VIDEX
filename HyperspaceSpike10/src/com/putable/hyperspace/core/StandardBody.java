package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.putable.hyperspace.interfaces.Body;

public class StandardBody implements Body {
	private Color mFg = Color.yellow;
	private Color mBg = Color.black;
	private Rectangle2D.Double mBounds = new Rectangle2D.Double(0,0,1,1);

	@Override
	public String toString() {
		return this.getClass().getName()+mBounds.toString();
	}
	@Override
	public Color getFg() {
		return mFg;
	}

	@Override
	public Color getBg() {
		return mBg;
	}

	@Override
	public Rectangle2D.Double getBounds2D() {
		return mBounds;
	}

	@Override
	public void setFg(Color col) {
		if (col == null) throw new NullPointerException();
		mFg = col;
	}

	@Override
	public void setBg(Color col) {
		if (col == null) throw new NullPointerException();
		mBg = col;
	}

	@Override
	public void setBounds(Rectangle2D rect) {
		if (rect == null) throw new NullPointerException();
		this.mBounds.setRect(rect);
	}

}
