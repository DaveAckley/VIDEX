package com.putable.hyperspace.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.putable.hyperspace.interfaces.FOEventHandler;

/**
 * OK, this demo spike wants to have a 9 bit vector centered in the given panel, and taking up 2/3s of the panel widith 
 * @author ackley
 *
 */
public class HIPSpike13 extends HIP {

	@Override
	public Rectangle2D getDiagramBounds() {
		return new Rectangle2D.Double(-5, -6, 24, 16);
	}

	@Override
	public FO buildDiagram() {
		Collector coll = new Collector(this) {
/*
			@Override
			public void refresh() {
				super.refresh();
				HIPSpike13.this.getJPanel().repaint();
			}
*/
		};

		//A RULER
		final VRuler vr = new VRuler(this);
		vr.setFontSize(0.72);
		vr.setCurX(-2);
		vr.setCenterOriginY(0.0);
		vr.setYTicDistance(2.0);
		vr.setYTics(5);
		coll.addChild(vr);
		coll.addFOEventHandler(new FOEventHandler() {
			@Override
			public boolean handleKey(FOEventKey foek) {
				if (foek.keyTyped('v') != null) {
					vr.setIsVisible(!vr.isVisible());
					return true;
				}
				return false;
			}
		});
		
		
		//THE BITVECTOR FUNCTION
		final int BITS = 9;
		final double SIDE = 1;
		BVHFunction bvhf = new SimpleMatch(this, BITS, SIDE, BITS);
		bvhf.setYFromScore();
		bvhf.evaluateAlternatives();
		coll.addChild(bvhf);
		return coll;
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
