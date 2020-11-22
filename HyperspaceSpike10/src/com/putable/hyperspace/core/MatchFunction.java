package com.putable.hyperspace.core;

import java.util.Iterator;

public class MatchFunction implements Function {
	
	private double [] mWeights;
	private double mAbsSum;
	public MatchFunction(int dims) {
		this(dims,new Iterable<Double>() {
			@Override
			public Iterator<Double> iterator() {
				return new Iterator<Double>() {
					@Override
					public boolean hasNext() { return true;	}
					@Override
					public Double next() { return 1.0; }
				};
			}
		});
	}
	public MatchFunction(int dims, Iterable<Double> itr) {
		mWeights = new double[dims];
		int i = 0;
		mAbsSum = 0;
		for (Double d : itr) {
			if (d != null) {
				mWeights[i] = d;
				mAbsSum += Math.abs(mWeights[i]);
			}
			else
				mWeights[i] = 0;
			if (++i >= mWeights.length) break;	
		}
	}
	@Override
	public double evaluate(BitVectorH bvh) {
		double score = 0;
		int i = 0;
		for (BitBox bb : bvh) {
			double weight = mWeights[i];
			score += bb.getValue().asInt()*weight;
			if (++i >= mWeights.length) i = 0;
		}
		return score;
	}

	@Override
	public Double getMax() {
		return mAbsSum;
	}

	@Override
	public Double getMin() {
		return -mAbsSum;
	}

}
