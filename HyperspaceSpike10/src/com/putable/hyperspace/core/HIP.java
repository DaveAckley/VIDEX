package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.Timer;

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
	public abstract AffineTransform configureDiagram(Rectangle2D boundsupdate) ;
	private AffineTransform mSpaceToPanel = null;
	public AffineTransform getSpaceToPanel() { return mSpaceToPanel; }
	
	public void configure(Rectangle2D panelbounds) {
		if (this.getRoot() == null) {
			this.setRoot(this.buildDiagram());
			this.setBounds(this.getDiagramBounds());
		}
		mSpaceToPanel = configureDiagram(panelbounds);
	}
	public HIPJPanel getJPanel() { return mPanel; }
	public void setOurPreferredSize(Dimension d) {
		this.getJPanel().setOurPreferredSize(d);
	}
	private HIPJPanel mPanel = new HIPJPanel(this);
	//private AffineTransform mLastAT = null;

	public boolean dispatchKeyEvent(KeyEvent e) {
		HIP hip = HIP.this;
        FO fo = hip.mPanel.getFOIfAny(mLastMousePosition);
        if (fo != null) {
        	FOEventKey foek = new FOEventKey(e, mLastMousePosition);
        	while (!fo.handle(foek)) {
        		fo = fo.getParent();
        		if (fo == null) return false;
        	}
        	return true;
        }
        return false;
	}

	private int mStepCount= 0;
	public int getStepCount() { return mStepCount; }
	
	/// HANDLE KEYBOARD EVENTS
	private class KeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			HIP.this.dispatchKeyEvent(e);
		}
		@Override
		public void keyTyped(KeyEvent e) {
			HIP.this.dispatchKeyEvent(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			HIP.this.dispatchKeyEvent(e);
		}
	}
	private Point2D mLastMousePosition = new Point2D.Double();
	private KeyListener mKeyListener = new KeyListener();
	
	/// HANDLE MOUSE EVENTS	
	private class MouseListener extends MouseAdapter {
		private Point2D mDragStart;
		
		@Override
		public void mouseMoved(MouseEvent e) {
	    	int x = e.getX();
	        int y = e.getY();
	        mLastMousePosition.setLocation(x, y);
		}
		@Override
	    public void mousePressed(MouseEvent e) {
	    	HIP hip = HIP.this;
	    	HIPJPanel jp = hip.mPanel;
	    	int x = e.getX();
	        int y = e.getY();
	        mLastMousePosition.setLocation(x, y);

	        if (e.getButton() == 3) {
	        	jp.setShowHitmap(true);
	        	return;
	        }

	        mDragStart = new Point2D.Double(x, y);
	        System.out.println("DRAG START "+x+", "+y);
	    }
	    @Override
	    public void mouseDragged(MouseEvent e) {
	        int x = e.getX();
	        int y = e.getY();
	        mLastMousePosition.setLocation(x, y);
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
	        mLastMousePosition.setLocation(x, y);
	        if (e.getButton() == 3) {
	        	hip.mPanel.setShowHitmap(false);
	        	return;
	        }

	        if (mDragStart == null) return;
	        double dx = x - mDragStart.getX();
	        double dy = y - mDragStart.getY();
	        System.out.println("DELTA DROP "+dx+", "+dy);
	        mDragStart = null;

	        Point2D at = new Point2D.Double(x,y);
	        FO fo = hip.mPanel.getFOIfAny(at);
	        if (fo != null) {
		        System.out.println("RELEASE "+fo+" ("+x+", "+y+")");
		        FOEventClick foec = new FOEventClick(e.getButton(), at);
		        fo.handle(foec);
	        }
	    }
	}
	private MouseListener mMouseListener = new MouseListener();

	/// DRAWING
	StandardHyperspaceRenderer mSHR = new StandardHyperspaceRenderer();

	//// UPDATING
	private final Timer mUpdateTimer;
	private final ActionListener mUpdateTask = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (HIP.this.mPanel.isShowing())
				HIP.this.cycle();
		}
		
	};

	private abstract class FOOp {
		protected final HIP mHIP;
		public FOOp(HIP hip) {
			mHIP = hip;
		}
		public abstract void op(FO fo) ;
	}
	private FOOp mTransformOp = new FOOp(this) {
		@Override
		public void op(FO fo) { fo.transform(mHIP);	}
	};
	private FOOp mStepOp = new FOOp(this) {
		@Override
		public void op(FO fo) { 
			fo.step(mHIP);	
		}
	};
	private void doTree(FO fo, FOOp fop) {
		fop.op(fo);
		for (Iterator<FO> itr = fo.kids(); itr.hasNext(); ) {
			FO kid = itr.next();
			doTree(kid,fop);
		}
	}
	private void doTransform() {// Derive body positions, colors, etc
		doTree(this.getRoot(),mTransformOp);  
	}
	private void doStep() { // Take actions
		doTree(this.getRoot(),mStepOp);
		
	}
	private void doDraw() { // Draw them
		this.getJPanel().repaint();
		
	}
	private void cycle() {
		FO root = this.getRoot();
		if (root == null) {
			System.out.println("HIP.cycle no root");
			return;
		}
		doTransform();
		doStep();
		doDraw();
		++this.mStepCount;
	}
	
	/// CTOR
	public HIP() {
		mPanel.setBackground(Color.black);
		mPanel.setForeground(Color.white);
		mPanel.addMouseListener(mMouseListener);
		mPanel.addMouseMotionListener(mMouseListener);
		mPanel.addKeyListener(mKeyListener);
		mPanel.setFocusable(true);
		mPanel.setFocusTraversalKeysEnabled(false);
		mPanel.requestFocusInWindow();
		mUpdateTimer = new Timer(100,mUpdateTask);
		mUpdateTimer.start();
	}
}
