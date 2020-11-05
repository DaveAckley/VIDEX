package com.putable.hyperspace.core;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class HitmappedJPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private FOHitmap mHitmap;
	private FO mRoot;
	private StandardHyperspaceRenderer mRenderer;
	
	public HitmappedJPanel(FO root) {
		super();
		throw new UnsupportedOperationException("UNIMPLEMENTED");
		/*
		mHitmap = new FOHitmap(200,100);
		mRoot = root;
		mRenderer = new StandardHyperspaceRenderer();
	*/
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		mHitmap.clear();
		mRenderer.setG2d(g2d);
		mRoot.draw(mRenderer);
	}
}
