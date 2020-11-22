package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;

import com.putable.hyperspace.interfaces.Body;
import com.putable.hyperspace.interfaces.Finite2DSpace;

public class VRuler extends AFO {
	@Override
	public boolean handle(FOEvent foe) {
		if (!(foe instanceof FOEventKey)) return false;
		FOEventKey foek = (FOEventKey) foe;
		KeyEvent ke = foek.mKeyEvent;
		if (ke.getID() != KeyEvent.KEY_TYPED) return false;
		char ch = ke.getKeyChar();
		if (ch == 'v') { 
			this.setIsVisible(false);
			return true;
		}
		System.out.println("VRULER '" + ch + "'");
		return false;
	}

	public double getCenterOriginY() {
		return mCenterOriginY;
	}
	public void setCenterOriginY(double mCenterOriginY) {
		this.mCenterOriginY = mCenterOriginY;
	}
	public double getYTicDistance() {
		return mYTicDistance;
	}
	public void setYTicDistance(double mYTicDistance) {
		this.mYTicDistance = mYTicDistance;
	}
	public int getYTics() {
		return mYTics;
	}
	public void setYTics(int mYTics) {
		this.mYTics = mYTics;
	}
	public double getFontSize() {
		return mFontSize;
	}
	private Font mLabelFont = new Font("Inconsolata", Font.PLAIN, 5);
	public void setFontSize(double fs) {
		if (fs != mFontSize) {
			mFontSize = fs;
			Font baseFont = new Font("Inconsolata", Font.PLAIN, 1);
			baseFont = baseFont.deriveFont(AffineTransform.getScaleInstance(mFontSize, mFontSize));
			Hashtable<TextAttribute, Object> map =
					new Hashtable<TextAttribute, Object>();

			/* Kerning makes the text spacing more natural */
	        map.put(TextAttribute.KERNING, 0);
			mLabelFont = baseFont.deriveFont(map);
		}
	}
	private double mCenterOriginY = 100;
	private double mYTicDistance = 10;
	private int mYTics = 2; //< Either side of center
	private double mFontSize = 0;

	public VRuler(Finite2DSpace f2) {
		super(f2);
		this.setFontSize(5);
		this.setCurBackgroundColor(Color.black);
		this.setCurForegroundColor(Color.white);
	}

	@Override
	public void setIsVisible(boolean viz) {
		if (viz != this.isVisible()) {
			super.setIsVisible(viz);
			this.setCurForegroundColor(viz ? Color.white : Color.black);
		}
	}

	@Override
	public void draw(HyperspaceRenderer hr) {
		if (!this.isVisible() && !this.isTweening()) return;
		Graphics2D g2d = hr.getGraphics2D();
		// We have (x,y) from AFO.  X is where we draw our line
		// Y is where we draw mCenterOriginY?
		Body body = this.getDisplayedBody();
		double x = body.getX();
		double y = body.getY();
		g2d.setColor(hr.getFg(this));
		final double side = 2*mFontSize;
		//g2d.fillRect(x-side/2, y-side/2, side, side);
		Font oldFont = g2d.getFont();
		g2d.setFont(mLabelFont);
		Rectangle2D rect = new Rectangle2D.Double(0, 0, 0, 0);
		for (int t = -mYTics; t <= mYTics; ++t) {
			double ty = y+t*mYTicDistance;
			rect.setRect(x, ty, side/5, side/50);
			g2d.fill(rect);
			String s = String.format("%d", (int)(mCenterOriginY+ty));

	        TextLayout tl = new TextLayout(s, mLabelFont, g2d.getFontRenderContext());
	        //Rectangle2D bounds =
//	        		new Rectangle2D.Float(0, -tl.getAscent(), tl.getAdvance(),
//	        				tl.getAscent() + tl.getDescent() /*+tl.getLeading()*/);
	        //System.out.println("TLLLLBBBB "+tl.getAscent()+" ddd "+tl.getDescent()+" lll "+tl.getLeading());
	        //System.out.println("BBBB "+bounds);
	        g2d.drawString(s, (float) (x-tl.getAdvance()), (float) (ty+(tl.getAscent()-tl.getDescent())/2));
		}
		g2d.setFont(oldFont);
//		System.out.println("VRULERDRW "+x+", "+y);
	}
/*
	@Override
	public void stepKids(HIP hip) {
		System.out.println("VRuler.stepKids()");
		
	}
*/
	@Override
	public Iterator<FO> kids() {
		return noKids();
	}

}
