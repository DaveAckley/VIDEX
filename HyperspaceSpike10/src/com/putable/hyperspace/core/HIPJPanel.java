package com.putable.hyperspace.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class HIPJPanel extends JPanel {
	private final HIP mHIP;
	private FOHitmap mHitmap;
	private boolean mShowHitmap = false;
	private boolean mDrawBounds = false;
	private Dimension mOurPreferredSize = new Dimension(200,100);
	public void setOurPreferredSize(Dimension d) {
		mOurPreferredSize = d;
	}
	public boolean isShowHitmap() { return mShowHitmap; }
	public void setShowHitmap(boolean show) { 
		if (show != mShowHitmap) {
			mShowHitmap = show;
			repaint();
		}
	}
	@Override
	public Dimension getPreferredSize() {
		if (this.isPreferredSizeSet()) return super.getPreferredSize();
		return this.mOurPreferredSize;
	}
	private void configureHitmap(Rectangle rect) {
		mHitmap = new FOHitmap(mHIP,rect.width,rect.height);
	}
	
	public FO getFOIfAny(Point2D pt) {
		return mHitmap.getFOAtPixelIfAny((int) pt.getX(), (int) pt.getY());
	}
	/**
	 * @param hip
	 */
	HIPJPanel(HIP hip) {
		this.mHIP = hip;
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Rectangle rect = HIPJPanel.this.getBounds();
				mHIP.configure(rect);
				configureHitmap(rect);
				repaint();
			}
		});
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.mHitmap == null) return; // too soon
		
		Graphics2D g2d = (Graphics2D) g;
		this.renderTo(g2d,false);
		this.renderTo(this.mHitmap.getGraphics2D(), true);

		if (mShowHitmap) this.mHitmap.paintImage(g2d);
		if (mDrawBounds) {
			Rectangle rect = this.getBounds();
			g2d.draw(rect);
		}
	}
	
	private void renderTo(Graphics2D g2d, boolean inHitmap) {
		if (this.mHIP.getSpaceToPanel() == null) return; // too soon
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);

		AffineTransform old = g2d.getTransform();
		g2d.transform(this.mHIP.getSpaceToPanel());

		this.mHIP.mSHR.setG2d(g2d);
		this.mHIP.mSHR.setIsInHitmap(inHitmap);
		this.mHIP.draw(this.mHIP.mSHR);
		g2d.setTransform(old);
	}
}