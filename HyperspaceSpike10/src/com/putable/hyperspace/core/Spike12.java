package com.putable.hyperspace.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Spike12 extends JPanel {
	public static final double MAX_X = 16_000_000;
	public static final double MAX_Y =  9_000_000;
	public static final int DESIRED_SCREEN = 1;
	private double mScale = -1;
	private List<FO> mFOs = new LinkedList<FO>();
	private StandardHyperspaceRenderer mSHR = new StandardHyperspaceRenderer(); 
	public Spike12(int width, int height) {
		throw new UnsupportedOperationException("DEIMPLEMENTED");
/*
		BitVectorH bvh =new BitVectorH(9);
		Random random = new Random();
		for (BitBox bb : bvh) {
			bb.setValue(BitValue.random(random, .5, true));
			bb.setOffsetAlternative(random.nextInt(2_000_000)-1_000_000);
		}
		bvh.setBitBoxSize(400_000);
		bvh.setPosition((int) MAX_X/4, (int) MAX_Y/3);
		this.mFOs.add(bvh);
		this.mMemo = new JLabel("--");
		this.mMemo.setBackground(Color.black);
		this.mMemo.setForeground(Color.yellow);
		this.setLayout(new BorderLayout());
		this.add(mMemo,BorderLayout.SOUTH);
		this.setBackground(Color.black);
		this.setForeground(Color.yellow);
		
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

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		mSHR.setG2d(g2d);
		AffineTransform old = g2d.getTransform();
		super.paintComponent(g);
		g2d.scale(mScale, mScale);
		g2d.translate(0, mSHR.getYOrigin()+2000000);
		for (FO fo : mFOs) {
			fo.draw(mSHR);
		}
		g2d.setTransform(old);
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
		int screen = Spike12.DESIRED_SCREEN;
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

		Spike12 s = new Spike12(rect.width,rect.height);
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
