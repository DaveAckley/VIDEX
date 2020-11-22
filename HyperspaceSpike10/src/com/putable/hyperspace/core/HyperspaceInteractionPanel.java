package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HyperspaceInteractionPanel extends JPanel {
	private boolean mShowHitmap = false;
	private AffineTransform mLastAT = null;
	public void setShowHitmap(boolean b) {
		mShowHitmap = b;
		repaint();
	}

	private class MyListener extends MouseAdapter {
		private Point2D mDragStart;
	    @Override
	    public void mousePressed(MouseEvent e) {
	    	HyperspaceInteractionPanel hip = HyperspaceInteractionPanel.this;
	    	int x = e.getX();
	        int y = e.getY();
	        Point2D pt = new Point2D.Double(x, y);
	        if (hip.mLastAT != null) {
	        	try {
	        		hip.mLastAT.inverseTransform(pt, pt);
	        	}
	        	catch (NoninvertibleTransformException e1) {
	        	}
	        }
	        System.out.println("AT "+x+", "+y+" -> "+ pt);
	        if (e.getButton() == 3) {
	        	hip.setShowHitmap(true);
	        	return;
	        }

	        FO fo = hip.mHitmap.getFOAtPixelIfAny(x, y);
	        if (fo != null)
		        System.out.println("HIT "+fo+" ("+x+", "+y+")");
	        mDragStart = new Point2D.Double(x, y);
	        System.out.println("DRAG START "+x+", "+y);
	    }
	    @Override
	    public void mouseDragged(MouseEvent e) {
	        int x = e.getX();
	        int y = e.getY();
	        if (mDragStart == null) return;
	        double dx = x - mDragStart.getX();
	        double dy = y - mDragStart.getY();
	        System.out.println("DELTA DRAG "+dx+", "+dy);
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    	HyperspaceInteractionPanel hip = HyperspaceInteractionPanel.this;
	        int x = e.getX();
	        int y = e.getY();
	        if (e.getButton() == 3) {
	        	hip.setShowHitmap(false);
	        	return;
	        }
	        if (mDragStart == null) return;
	        double dx = x - mDragStart.getX();
	        double dy = y - mDragStart.getY();
	        System.out.println("DELTA DROP "+dx+", "+dy);
	        mDragStart = null;
	    }
	}
	private FOHitmap mHitmap;
	private FO mRoot;
	private MyListener mMouseListener = new MyListener();
	public static final double MAX_X = 16_000_000;
	public static final double MAX_Y =  9_000_000;
	public static final int DESIRED_SCREEN = 1;
	private JLabel mMemo;
	private Dimension mWindowSize;
	private double mScale = -1;
	private Map<Integer,FO> mFOs = new LinkedHashMap<Integer,FO>();
	private StandardHyperspaceRenderer mSHR = new StandardHyperspaceRenderer(); 
	public HyperspaceInteractionPanel(int width, int height) {
		this.mWindowSize = new Dimension(width,height);
		throw new UnsupportedOperationException();
		/*
	    this.mHitmap = new FOHitmap(width,height);
		BitVectorH bvh =new BitVectorH(9);
		Random random = new Random();
		for (BitBox bb : bvh) {
			bb.setValue(BitValue.random(random, .5, true));
			bb.setOffsetAlternative(random.nextInt(2_000_000)-1_000_000);
		}
		bvh.setBitBoxSize(400_000);
		bvh.setPosition((int) MAX_X/4, (int) MAX_Y/3);
		this.addFO(bvh);
		VRuler vr = new VRuler();
		this.addFO(vr);
		this.mMemo = new JLabel("--");
		this.mMemo.setBackground(Color.black);
		this.mMemo.setForeground(Color.yellow);
		this.setLayout(new BorderLayout());
		this.add(mMemo,BorderLayout.SOUTH);
		this.setBackground(Color.black);
		this.setForeground(Color.yellow);
		this.addMouseListener(this.mMouseListener);
		this.addMouseMotionListener(this.mMouseListener);
		final Iterator<Double> itr = new Iterator<Double>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Double next() {
				return random.nextDouble()*20-10;
			}

		};
		MatchFunction mf = new MatchFunction(9,new Iterable<Double>() {

			@Override
			public Iterator<Double> iterator() {
				return itr;
			}
			
		});
		this.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent ce) {
		    	HyperspaceInteractionPanel hip = HyperspaceInteractionPanel.this; 
		    	int w = hip.getWidth();
		    	int h = hip.getHeight();
		    	System.out.println("RESIZED "
		    			+w+", "
		    			+h);
		    	hip.mHitmap.resizeHitmap(w, h);
		    }
		});
		double max = mf.getMax();
		double min = mf.getMin();
		double range = max-min;
		this.mSHR.setYOrigin((max+min)/2);
		if (mScale < 0) {
			double scalex = mWindowSize.width / range; 
			double scaley = mWindowSize.height / range;
			mScale = Math.min(scalex, scaley)/100_000;
			System.out.println("mScale="+mScale + " ("+mWindowSize+")");
			System.out.println(this.getLocation());
		}

		mSHR.setYScale(mScale);
		bvh.evaluate(mf);
		System.out.println(mf);
*/
	}
/*
	private void addFO(FO fo) {
		this.mFOs.put(fo.getIndex(), fo);
	}
*/
	private void paintFOs() {
		Graphics2D g2d = mSHR.getG2d();
		AffineTransform old = g2d.getTransform();
		this.setBackground(Color.DARK_GRAY);
		this.setForeground(Color.yellow);
		super.paintComponent(g2d);
		//g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2d.scale(mScale, mScale);
		g2d.translate(0, mSHR.getYOrigin()+2000000);
		this.mLastAT = g2d.getTransform();
		for (FO fo : mFOs.values()) {
			g2d.setBackground(mSHR.getBg(fo));
			g2d.setColor(mSHR.getFg(fo));
			fo.draw(mSHR);
		}
		g2d.setTransform(old);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		mSHR.setG2d(g2d);
		mSHR.setIsInHitmap(false);
		this.paintFOs();

		g2d.fill3DRect(1000, 600, 30, 40, true);
		mSHR.setG2d(this.mHitmap.getGraphics2D());
		this.mHitmap.getGraphics2D().setColor(Color.green);
		this.mHitmap.getGraphics2D().fillRect(0, 0, 2000, 2000);
		mSHR.setIsInHitmap(true);
		this.paintFOs();
		
		if (this.mShowHitmap) 
			this.mHitmap.paintImage(g2d);
	}
	
	private static final long serialVersionUID = 1L;

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("SPIKE10");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
//		Dimension dim = toolkit.getScreenSize();
	

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		for (GraphicsDevice gc : gd) {
			System.out.println(gc + " defb " + gc.getDefaultConfiguration().getBounds());
		}
		GraphicsDevice thegd;
		int screen = HyperspaceInteractionPanel.DESIRED_SCREEN;
		if(screen > -1 && screen < gd.length ) {
			thegd = gd[screen];
		} else if( gd.length > 0 ) {
			thegd = gd[0];
			frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
		} else {
			throw new RuntimeException( "No Screens Found" );
		}
		GraphicsConfiguration [] gcs  = thegd.getConfigurations();
		for (GraphicsConfiguration gc : gcs) {
			System.out.println(gc + " bd " + gc.getBounds());
		}
		Rectangle rect = thegd.getDefaultConfiguration().getBounds();
		System.out.println(thegd + " rect " + rect);
		frame.setLocation(rect.x, frame.getY());
		frame.setSize(rect.width,rect.height);

		HyperspaceInteractionPanel s = new HyperspaceInteractionPanel(rect.width,rect.height);
		frame.getContentPane().add(s); // BorderLayout center

		// Go for fullscreen
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);
		frame.setVisible(true);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
