package com.putable.hyperspace.core;

/** 
 * Updatable Function Object base class.
 * 
 * @author ackley
 *
 */
public abstract class UFO /*extends AFO*/ {
/*
	private final AFO mPrev;
	private static final double[] mSTEPS = {
			0.00,
			0.05,
			0.20,
			0.50,
			0.80,
			0.95,
			1.00
	};
	private int mBackStep = -1;
	private int mStep = 0;
	private double fracNew() {
		return mSTEPS[mStep];
	}
	private void snapBack() {
		mPrev.setX(super.getX());
		mPrev.setY(super.getY());
		mPrev.setWidth(super.getWidth());
		mPrev.setHeight(super.getHeight());
		mPrev.setForegroundColor(super.getForegroundColor());
		mPrev.setBackgroundColor(super.getBackgroundColor());
		if (mStep != mBackStep) {
			if (mStep == mSTEPS.length-1) mStep = 0; // Start slow if stopped
			else if (mStep > 0) --mStep;
			mBackStep = mStep;
		}
	}
	private double interpDouble(double oldd, double newd) {
		return (1.0-fracNew())*oldd + fracNew()*newd;
	}
	private int interpInt(int oldi, int newi) {
		return (int) interpDouble(oldi, newi);
	}
	private Color interpColor(Color oldc, Color newc) {
		return new Color(
				interpInt(oldc.getRed(),newc.getRed()),
				interpInt(oldc.getGreen(),newc.getGreen()),
				interpInt(oldc.getBlue(),newc.getBlue())
				);
	}
	@Override
	public void stepKids() {
		// Assume we have no kids
	}
	@Override
	public void step() {
		if (mStep < mSTEPS.length-1) ++mStep;
		mBackStep = -1;
	}
	@Override
	public double getX() {
		return interpDouble(mPrev.getX(),super.getX());
	}
	@Override
	public double getY() {
		return interpDouble(mPrev.getY(),super.getY());
	}
	@Override
	public double getWidth() {
		return interpDouble(mPrev.getWidth(),super.getWidth());
	}
	@Override
	public double getHeight() {
		return interpDouble(mPrev.getHeight(),super.getHeight());
	}
	@Override
	public Color getForegroundColor() {
		return interpColor(mPrev.getForegroundColor(),super.getForegroundColor());
	}
	@Override
	public Color getBackgroundColor() {
		return interpColor(mPrev.getBackgroundColor(),super.getBackgroundColor());
	}
	
	@Override
	public void setX(double x) {
		snapBack();
		super.setX(x);
	}
	@Override
	public void setY(double y) {
		snapBack();
		super.setY(y);
	}
	@Override
	public void setWidth(double w) {
		snapBack();
		super.setWidth(w);
	}
	@Override
	public void setHeight(double h) {
		snapBack();
		super.setHeight(h);
	}
	@Override
	public void setForegroundColor(Color col) {
		snapBack();
		super.setForegroundColor(col);
	}
	@Override
	public void setBackgroundColor(Color col) {
		snapBack();
		super.setBackgroundColor(col);
	}
	public UFO(Finite2DSpace fs) {
		super(fs); 
		this.mPrev = new AFO(fs) {
			@Override
			public void step() {
				throw new IllegalStateException("Don't step me");
			}
			@Override
			public void stepKids() {
				throw new IllegalStateException("Don't step my kids");
			}
			@Override
			public void draw(HyperspaceRenderer hr) {
				throw new IllegalStateException("Don't draw me");
			}
		};
	}
	
	

	*/
}
