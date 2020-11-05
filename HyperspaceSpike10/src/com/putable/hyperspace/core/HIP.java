package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public abstract class HIP extends StandardFinite2DSpace  {
	/// SUBCLASS REQUIREMENTS
	/**
	 * @return non-null root of the diagram
	 */
	public abstract FO buildDiagram() ; 
	/**
	 * Define the bounds of the diagram
 	 */
	public abstract Rectangle2D getDiagramBounds() ;
	
	/** 
	 * (re)Produce a transform to map the diagram to the given bounds
	 * @param bounds new origin and size of the drawing panel
	 */
	public abstract AffineTransform configureDiagram(Rectangle2D bounds) ;
	private AffineTransform mSpaceToPanel = null;
	
	public void configure(Rectangle2D panelbounds) {
		if (this.getRoot() == null) {
			this.setRoot(this.buildDiagram());
			this.setBounds(this.getDiagramBounds());
		}
		mSpaceToPanel = configureDiagram(panelbounds);
	}
	public JPanel getJPanel() { return mPanel; }

	private JPanel mPanel = new JPanel() {

		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			HIP hip = HIP.this;
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform old = g2d.getTransform();
			g2d.setTransform(hip.mSpaceToPanel);

			mSHR.setG2d(g2d);
			mSHR.setIsInHitmap(false);
			hip.draw(mSHR);

			g2d.fill3DRect(1000, 600, 30, 40, true);
			/*			mSHR.setG2d(this.mHitmap.getGraphics2D());
			this.mHitmap.getGraphics2D().setColor(Color.green);
			this.mHitmap.getGraphics2D().fillRect(0, 0, 2000, 2000);
			mSHR.setIsInHitmap(true);
			this.paintFOs();
			
			if (this.mShowHitmap) 
				this.mHitmap.paintImage(g2d);
	*/
			g2d.setTransform(old);
		}
	};
	private AffineTransform mLastAT = null;
	
	/// HANDLE MOUSE EVENTS	
	private class MouseListener extends MouseAdapter {
		private Point2D mDragStart;
	    @Override
	    public void mousePressed(MouseEvent e) {
	    	HIP hip = HIP.this;
	    	JPanel jp = hip.mPanel;
	    	int x = e.getX();
	        int y = e.getY();
	        Point2D pt = new Point2D.Double(x, y);
	        Graphics2D lastG2d = hip.mSHR.getG2d();
	        if (hip.mLastAT != null) {
	        	try {
	        		hip.mLastAT.inverseTransform(pt, pt);
	        	}
	        	catch (NoninvertibleTransformException e1) {
	        	}
	        }
	        System.out.println("AT "+x+", "+y+" -> "+ pt);
	        /*
	        if (e.getButton() == 3) {
	        	hip.setShowHitmap(true);
	        	return;
	        }
	         */	
	        FO fo = hip.getFOIfAny(new Point2D.Double(x,y));
	        if (fo != null)
		        System.out.println("HIT "+fo+" ("+x+", "+y+")");
	        mDragStart = new Point2D.Double(x, y);
	        System.out.println("DRAG START "+x+", "+y);
	    }
	    @Override
	    public void mouseDragged(MouseEvent e) {
	    	HIP hip = HIP.this;
	        int x = e.getX();
	        int y = e.getY();
	        if (mDragStart == null) return;
	        double dx = x - mDragStart.getX();
	        double dy = y - mDragStart.getY();
	        System.out.println("DELTA DRAG "+dx+", "+dy);
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    	HIP hip = HIP.this;
	        int x = e.getX();
	        int y = e.getY();
	        /*
	        if (e.getButton() == 3) {
	        	hip.setShowHitmap(false);
	        	return;
	        }
	        */
	        if (mDragStart == null) return;
	        double dx = x - mDragStart.getX();
	        double dy = y - mDragStart.getY();
	        System.out.println("DELTA DROP "+dx+", "+dy);
	        mDragStart = null;
	    }
	}
	private MouseListener mMouseListener = new MouseListener();

	/// DRAWING
	private StandardHyperspaceRenderer mSHR = new StandardHyperspaceRenderer();

	private static final long serialVersionUID = 1L;

	/// CTOR
	public HIP() {
		mPanel.setBackground(Color.black);
		mPanel.setForeground(Color.white);
	}
}
