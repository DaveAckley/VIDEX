package com.putable.hyperspace.core;

public interface Function {
	public double evaluate(BitVectorH bvh) ;

	public default double evaluateDelta(BitVectorH bvh, int index, BitValue alternative) {
		double asIs = evaluate(bvh);
		BitValue old = bvh.getBitValue(index);
		bvh.setBitValue(index, old.opposite());
		double flipped = evaluate(bvh);
		bvh.setBitValue(index, old);
		return flipped - asIs;
	}

	public Double getMax() ;
	public Double getMin() ;
}
