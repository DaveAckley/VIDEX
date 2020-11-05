package com.putable.hyperspace.core;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class StandardFinite2DSpace implements Finite2DSpace {
	private Rectangle2D mBounds = new Rectangle2D.Double(0, 0, 1, 1);
	private FO mFORoot;
	private FOHitmap mHitmap;

	private int mAFOHitmapNextIndex = 0x000002;
	private final int AFO_MAP_INCREMENT = 0x1234;
	private Map<Integer,FO> mGlobalFOMap = new LinkedHashMap<Integer,FO>();

	@Override
	public int getIndexForFO(FO fo) {
		int idx = fo.getIndex();
		if (idx != 0 && fo.getSpace() == this) return idx;
		if (idx != 0 || fo.getSpace() != this)
			throw new IllegalStateException();
		idx = mAFOHitmapNextIndex;
		mAFOHitmapNextIndex+=AFO_MAP_INCREMENT;
		if (mAFOHitmapNextIndex > 0x00ffffff)
			throw new ArrayIndexOutOfBoundsException();
		
		mGlobalFOMap.put(idx, fo);
		return idx;
	}
	
	@Override
	public FO getFOForIndex(int index) {
		return mGlobalFOMap.get(index);
	}

	
	@Override
	public Rectangle2D getBounds() {
		return (Rectangle2D) mBounds.clone();
	}

	@Override
	public FO getFOIfAny(Point2D at) {
		return mHitmap.getFOAtPixelIfAny((int) at.getX(), (int) at.getY());
	}

	@Override
	public FO getRoot() {
		return mFORoot;
	}
	
	public void setRoot(FO r) {
		if (r == null) throw new IllegalArgumentException();
		if (mFORoot != null) throw new IllegalStateException();
		mFORoot = r;
	}
	
	public void setBounds(Rectangle2D rect) {
		mBounds.setRect(rect);
	}

	@Override
	public void draw(HyperspaceRenderer hr) {
		this.getRoot().draw(hr);
	}

}
