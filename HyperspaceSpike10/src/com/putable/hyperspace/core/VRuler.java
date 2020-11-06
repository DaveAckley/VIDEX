package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.util.Hashtable;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class VRuler extends AFO {
	private double mCenterOriginY = 1_000_000;
	private double mYTicDistance = 1_000_000; //< 'Pixel' distance per tic
	private int mYTics = 2; //< Either side of center
	
	public VRuler(Finite2DSpace f2) {
		super(f2);
		this.setBackgroundColor(Color.black);
		this.setForegroundColor(Color.white);
	}
	@Override
	public void draw(HyperspaceRenderer hr) {
		Graphics2D g2d = hr.getGraphics2D();
		// We have (x,y) from AFO.  X is where we draw our line
		// Y is where we draw mCenterOriginY?
		int x = this.getX();
		int y = this.getY();
		g2d.setColor(getForegroundColor());
		final int side = 500_000;
		//g2d.fillRect(x-side/2, y-side/2, side, side);
		Font oldFont = g2d.getFont();
		//Font newFont = oldFont.deriveFont(oldFont.getSize() * 100_000F);
		Font baseFont = new Font("Inconsolata", Font.PLAIN, 460_000); 
		Hashtable<TextAttribute, Object> map =
				new Hashtable<TextAttribute, Object>();

		/* Kerning makes the text spacing more natural */
        map.put(TextAttribute.KERNING, 0);
		Font newFont = baseFont.deriveFont(map);

		g2d.setFont(newFont);
		for (int t = -mYTics; t <= mYTics; ++t) {
			int ty = (int) (y+t*mYTicDistance);
			g2d.fillRect(x, ty, side, side/50);
			String s = String.format("%d", (int)(mCenterOriginY+ty));
			g2d.drawString(s, x, ty);
		}
		g2d.setFont(oldFont);
		System.out.println("VRULERDRW "+x+", "+y);
	}

}
