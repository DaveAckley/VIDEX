package com.putable.hyperspace.core;

import java.util.Arrays;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class BitVectorH extends AFO implements Iterable<BitBox> {
	private double mAltScale = .75; // < Size of alt relative to hero
	private double mYScale = 1;
	private double mYOrigin = 0;
	private double mBitBorderSize;
	private BitBox[] mBits = new BitBox[0];
	private BitBox mHero;

	public int getBitCount() { return mBits.length; }
	public BitBox getBitBox(int index) {
		return mBits[index]; // or ArrayBoundsException
	}
	public BitVectorH(Finite2DSpace f2, int count, double side) {
		super(f2);
		mBitBorderSize = side / 20;
		mHero = new BitBox(f2, side, mBitBorderSize);
		resize(count);
		this.setCurBackgroundColor(BitValue.BIT_INVISIBLE.getFill());
		this.setCurForegroundColor(BitValue.BIT_INVISIBLE.getFill());
	}

	public BitValue getBitValue(int index) {
		if (index < 0)
			throw new IllegalArgumentException();
		if (index >= mBits.length)
			return BitValue.BIT_FIXED_UNKNOWN;
		return mBits[index].getValue();
	}

	/** Note this also sets the alternative to bv.opposite() */
	public void setBitValue(int index, BitValue bv) {
		if (index < 0)
			throw new IllegalArgumentException();
		if (index >= mBits.length && bv != BitValue.BIT_FIXED_UNKNOWN)
			throw new IllegalArgumentException();
		mBits[index].setValue(bv);

	}

	public void evaluate(Function func) {
		double score = func.evaluate(this);
		this.setCurY(score);
		for (BitBox bb : mBits) {
			BitValue cur = bb.getValue();
			bb.setValue(cur.opposite());
			double altscore = func.evaluate(this);
			bb.setValue(cur);
			bb.getAlternative().setCurY(altscore);
		}
	}

	@Override
	public void setCurX(double x) {
		if (x != this.getCurX()) { // Just comparing doubles anyway, since this is just an optimization
			super.setCurX(x);
			reconfigureX();
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

/*	@Override
	public void setY(double y) {
		if (y != this.getY()) {
			super.setY(y);
		}
	}
*/
	public void setBitBoxSize(double size) {
		if (size != mHero.getSize()) {
			mHero.setSize(size);
			reconfigureX();
		}
	}

	public void setBitBoxBorder(int bsize) {
		if (bsize != mHero.getBorder()) {
			mHero.setBorder(bsize);
			reconfigureX();
		}
	}

	public void reconfigureX() {
		double size = mHero.getSize();
		for (int i = 0; i < mBits.length; ++i) {
			mBits[i].setSize(size);
			mBits[i].setCurX(this.getCurX() + i * size);
			mBits[i].setCurY(this.getCurY());
			BitBox alt = mBits[i].getAlternative();
			if (alt != null) {
				double altsize = size * this.mAltScale;
				alt.setSize(altsize);
				alt.setCurX(this.getCurX() + i * size + (size-altsize)/2);
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
				mBits[i] = new BitBox(this.getSpace(), 1, 0);
				mBits[i].copy(mHero, false);
				mBits[i].setParent(this);
				mBits[i].setAlternative(new BitBox(this.getSpace(), 1, this.mBitBorderSize));
			}
		}
		reconfigureX();
	}

	@Override
	public Iterator<FO> kids() {
		return new Iterator<FO>() {
			private Iterator<BitBox> mItr = Arrays.asList(mBits).iterator();
			@Override
			public boolean hasNext() {
				return mItr.hasNext();
			}

			@Override
			public FO next() {
				BitBox bb = mItr.next();
				if (bb == null) throw new ArrayIndexOutOfBoundsException();
				return bb;
			}
			
		};
	}
/*	
	@Override
	public void step(HIP hip) {
		this.stepKids(hip);
	}
*/
/*
	@Override
	public void stepKids(HIP hip) {
		for (BitBox bb : mBits) {
			bb.step(hip);
		}
	}
*/
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

	@Override
	public boolean handleKey(FOEventKey foek) {
		Character ch = foek.keyTyped('b','B');
		if (ch != null) {
			for (BitBox bb : mBits) {
				bb.setAltActive(ch == 'B');
			}
			return true;
		}
		return false;
	}
}
