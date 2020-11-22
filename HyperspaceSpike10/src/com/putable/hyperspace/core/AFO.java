package com.putable.hyperspace.core;

import java.awt.Color;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.Finite2DSpace;

public abstract class AFO implements FO {
	protected final Finite2DSpace mSpace;

	public Iterator<FO> noKids() {
		return new Iterator<FO>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public FO next() {
				throw new ArrayIndexOutOfBoundsException();
			}
		};
	}
	
	public Finite2DSpace getSpace() { return mSpace; }

	/*public abstract void stepKids(HIP hip) ;*/

	@Override
	public boolean handle(FOEvent foe) {
		System.out.println("handle "+foe);
		return false;
	}
	private StandardBody mCurBody = new StandardBody();
	private StandardBody mDisplayedBody = new StandardBody();
	
	@Override
	public Body getCurBody() {
		return this.mCurBody;
	}

	@Override
	public Body getDisplayedBody() {
		return mDisplayedBody;
	}

	private int mTweenIndex = 0;
	private Tweener mTweener = Tweener.getStandard();
	public boolean isTweening() {
		return mTweener.isTweenStep(mTweenIndex);
	}
	public void retween() {
		mTweenIndex = mTweener.retweenOnChange(mTweenIndex);
	}
	
	@Override
	public void transform(HIP hip) {
		this.mTweener.tweenBodyInPlace(this.getDisplayedBody(), this.getCurBody(), mTweenIndex);
		++this.mTweenIndex;
	}

	public void setCurY(double y) {
		if (this.getCurBody().getY() != y) {
			retween();
			this.getCurBody().setY(y);
		}
			
	}
	public void setCurX(double x) {
		if (this.getCurBody().getX() != x) {
			retween();
			this.getCurBody().setX(x);
		}
	}
	public void setCurWidth(double w) {
		if (this.getCurBody().getWidth() != w) {
			retween();
			this.getCurBody().setWidth(w);
		}
	}
	public void setCurHeight(double h) {
		if (this.getCurBody().getHeight() != h) {
			retween();
			this.getCurBody().setHeight(h);
		}
	}
	
	public void setCurBackgroundColor(Color col) {
		if (!this.getCurBody().getBg().equals(col)) {
			retween();
			this.getCurBody().setBg(col);
		}
	}
	public void setCurForegroundColor(Color col) {
		if (!this.getCurBody().getFg().equals(col)) {
			retween();
			this.getCurBody().setFg(col);
		}
	}
	public double getCurWidth() {
		return getCurBody().getWidth();
	}
	public double getCurHeight() {
		return getCurBody().getHeight();
	}
	public double getCurX() {
		return getCurBody().getX();
	}
	public double getCurY() {
		return getCurBody().getY();
	}
	public Color getCurBackgroundColor() {
		return getCurBody().getBg();
	}
	public Color getCurForegroundColor() {
		return getCurBody().getFg();
	}

	public void step(HIP hip) {
		/* By default do nothing */
	}

	private int mIndex;
	private FO mParent = null;
	private boolean mIsActive = true;
	private boolean mIsVisible = true;
	
	@Override
	public boolean isActive() {
		return mIsActive;
	}

	public void setIsActive(boolean mIsActive) {
		if (this.mIsActive != mIsActive) {
			this.mIsActive = mIsActive;
			this.retween();
		}
	}

	@Override
	public boolean isVisible() {
		return mIsVisible;
	}

	public void setIsVisible(boolean mIsVisible) {
		if (this.mIsVisible != mIsVisible) {
			this.mIsVisible = mIsVisible;
			this.retween();
		}
	}

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

	public int getHitmapIndex() { 
		return mIndex; 
	}

}
