package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import com.putable.hyperspace.interfaces.Body;

public class Tweener {
	private static Tweener mStandardTweener = new Tweener();
	public static Tweener getStandard() {
		return mStandardTweener;
	}
	private final double[] mDELTA_FRACS = {
			0.0,
			0.05, 
			0.157895,
			0.375,
			0.6,
			0.75,
			1.0
	};
	public boolean isTweenStep(int step) {
		return (step >= 0) && (step < mDELTA_FRACS.length);
	}
	public int retweenOnChange(int step) {
		if (step < 0)   // Haven't changed in ages
			return 0;   // Now you're starting
		if (step == 0)  // Just starting 
			return 0;   // stay that way
		if (step >= mDELTA_FRACS.length) // Already done
			return 0;                    // start over
		if (step > mDELTA_FRACS.length/2)  // If slowing down 
			return mDELTA_FRACS.length/2;  // speed up
		return step;          // Keep cruising
	}
	
	public double tweenDouble(double prev, double target, int step) {
		if (step < 0) return prev;
		if (step >= mDELTA_FRACS.length) return target;
		return prev+(target-prev)*mDELTA_FRACS[step];
	}
	
	public Color tweenColor(Color prev, Color target, int step) {
		return new Color(
				(int) tweenDouble(prev.getRed(),target.getRed(),step),
				(int) tweenDouble(prev.getGreen(),target.getGreen(),step),
				(int) tweenDouble(prev.getBlue(),target.getBlue(),step));
	}
	public void tweenRectInPlace(Rectangle2D prev, Rectangle2D targ, int step) {
		prev.setRect(
				tweenDouble(prev.getX(),targ.getX(),step),
				tweenDouble(prev.getY(),targ.getY(),step),
				tweenDouble(prev.getWidth(),targ.getWidth(),step),
				tweenDouble(prev.getHeight(),targ.getHeight(),step)
				);
	}
	public void tweenBodyInPlace(Body prev, Body targ, int step) {
		tweenRectInPlace(prev.getBounds2D(), targ.getBounds2D(), step);
		prev.setBg(tweenColor(prev.getBg(),targ.getBg(),step));
		prev.setFg(tweenColor(prev.getFg(),targ.getFg(),step));
	}
}
