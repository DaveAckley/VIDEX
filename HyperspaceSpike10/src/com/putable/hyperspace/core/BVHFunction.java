package com.putable.hyperspace.core;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.Finite2DSpace;

public abstract class BVHFunction extends BitVectorH implements Function {
	@Override
	public void step(HIP hip) {
		this.setYFromScore();
		this.evaluateAlternatives();
	}
	public void setYFromScore() {
		double score = this.evaluate();
		this.setCurY(score);

		for (int i = 0; i < this.getBitCount(); ++i) {
			this.getBitBox(i).setCurY(score);
		}
		//System.out.println("EVALUATED TO Y "+this.getCurBody());
	}
	
	public void evaluateAlternatives() {
		for (int i = 0; i < this.getBitCount(); ++i) {
			this.evaluateAlternative(i);
		}
	}
	
	public void evaluateAlternative(int index) {
		Body cur = this.getCurBody();
		double base = cur.getY();
		BitBox bb = this.getBitBox(index);
		BitBox altbb = bb.getAlternative();
		if (altbb != null) {
			BitValue bv = bb.getValue();
			double alt = this.evaluateDelta(this, index, bv.opposite());
			altbb.setCurY(base+alt);
		}
	}

	public abstract double evaluate() ;
	public BVHFunction(Finite2DSpace f2, int count, double side) {
		super(f2, count, side);
	}
}
