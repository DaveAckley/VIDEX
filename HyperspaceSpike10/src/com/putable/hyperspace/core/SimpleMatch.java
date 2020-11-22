package com.putable.hyperspace.core;

import java.util.Random;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class SimpleMatch extends BVHFunction {
	private final int mBITS;
	private MatchFunction mMatchTo;

	SimpleMatch(Finite2DSpace f2, int count, double side, int bits) {
		super(f2, count, side);
		this.mBITS = bits;
		mMatchTo = new MatchFunction(bits);
		Random random = new Random();
		for (BitBox bit : this) {
			bit.setValue(BitValue.random(random, .5, true));
		}
	}

	@Override
	public double evaluate(BitVectorH bvh) {
		return mMatchTo.evaluate(bvh);
	}

	@Override
	public Double getMax() {
		return (double) mBITS;
	}

	@Override
	public Double getMin() {
		return 0.0;
	}

	@Override
	public double evaluate() {
		return evaluate((BitVectorH) this);
	}
}