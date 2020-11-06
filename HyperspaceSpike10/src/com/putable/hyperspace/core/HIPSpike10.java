package com.putable.hyperspace.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * OK, this demo spike wants to have a 9 bit vector centered in the given panel, and taking up 2/3s of the panel widith 
 * @author ackley
 *
 */
public class HIPSpike10 extends HIP {

	@Override
	public Rectangle2D getDiagramBounds() {
//		return new Rectangle2D.Double(-5_000_000, -5_000_000, 10_000_000, 10_000_000);
		return new Rectangle2D.Double(-5_001_000, -5_001_000, 10_000_000, 10_000_000);
	}

	@Override
	public FO buildDiagram() {
		Collector root = new Collector(this);

		//A RULER
		VRuler vr = new VRuler(this);
		vr.setX(-2_000_000);
		root.addChild(vr);
		
		//THE BITVECTOR
		BitVectorH bvh =new BitVectorH(this,9);
		Random random = new Random();
		for (BitBox bb : bvh) {
			bb.setValue(BitValue.random(random, .5, true));
			bb.setY(0);
			bb.getAlternative().setY(random.nextInt(4_000_000)-2_000_000);
		}
		root.addChild(bvh);
		return root;
	}

	@Override
	public AffineTransform configureDiagram(Rectangle2D panelbounds) {
		Rectangle2D spacebounds = this.getBounds();
		double sxctr = spacebounds.getCenterX();
		double syctr = spacebounds.getCenterY();
		double swidth = spacebounds.getWidth();
		double sheight= spacebounds.getHeight();
		System.out.println("acoigured "+panelbounds + " our space "+this.getBounds());
		System.out.println("our space cx"+sxctr);
		System.out.println("our space cy"+syctr);
		double pxctr = panelbounds.getCenterX();
		double pyctr = panelbounds.getCenterY();
		double pwidth = panelbounds.getWidth();
		double pheight= panelbounds.getHeight();
		double s2pwratio = swidth/pwidth;
		double s2phratio = sheight/pheight;
		double minratio = Math.max(s2pwratio, s2phratio);
		AffineTransform at = AffineTransform.getTranslateInstance(-sxctr, -syctr);
		at.scale(1/minratio, 1/minratio);
		at.translate(pxctr, pyctr);
		System.out.println("Wrat " + s2pwratio + " Hrat "+ s2phratio + " at "+ at);
		return at;
	}

}
