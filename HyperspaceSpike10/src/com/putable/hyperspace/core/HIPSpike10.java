package com.putable.hyperspace.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
		return new Rectangle2D.Double(-20, -20, 100, 100);
	}

	@Override
	public FO buildDiagram() {
		Collector root = new Collector(this);

		//A RULER
		VRuler vr = new VRuler(this);
		vr.setCurX(-10);
		root.addChild(vr);
		
		//THE BITVECTOR
		BitVectorH bvh =new BitVectorH(this,9,8);
		Random random = new Random();
		for (BitBox bb : bvh) {
			bb.setValue(BitValue.random(random, .5, true));
			bb.setCurY(0);
			bb.getAlternative().setCurY(random.nextInt(80)-40);
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
		double p2swratio = pwidth/swidth;
		double p2shratio = pheight/sheight;
		double minratio = Math.min(p2swratio, p2shratio);
		AffineTransform at = AffineTransform.getTranslateInstance(pxctr, pyctr);
		at.scale(minratio, minratio);
		at.translate(-sxctr, -syctr);
		System.out.println("sctr "+at.transform(new Point2D.Double(sxctr, syctr), null));
		System.out.println("(0,0) "+at.transform(new Point2D.Double(0, 0), null));
		System.out.println("Wrat " + p2swratio + " Hrat "+ p2shratio + " at "+ at);
		return at;
	}

}
