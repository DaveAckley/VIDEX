package com.putable.hyperspace.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Spike10 extends JPanel {
	public static final double MAX_X = 16_000_000;
	public static final double MAX_Y =  9_000_000;
	public static final int DESIRED_SCREEN = 1;
	private JLabel mMemo;
	private Dimension mWindowSize;
	private double mScale = -1;
	private List<FO> mFOs = new LinkedList<FO>();
	public Spike10(int width, int height) {
		this.mWindowSize = new Dimension(width,height);
		throw new UnsupportedOperationException("LEFT TO ROT");
		/*
		this.mFOs.add(new BitBox());
		this.mMemo = new JLabel("--");
		this.mMemo.setBackground(Color.black);
		this.mMemo.setForeground(Color.yellow);
		this.setLayout(new BorderLayout());
		this.add(mMemo,BorderLayout.SOUTH);
		this.setBackground(Color.black);
		this.setForeground(Color.yellow);
		*/
	}
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		StandardHyperspaceRenderer shr = new StandardHyperspaceRenderer();
		shr.setG2d(g2d);
		shr.setYOrigin(0);
		shr.setYScale(1);
		
		if (mScale < 0) {
			double scalex = mWindowSize.width / MAX_X; 
			double scaley = mWindowSize.height / MAX_Y;
			mScale = Math.min(scalex, scaley);
			System.out.println("mScale="+mScale + " ("+mWindowSize+")");
			System.out.println(this.getLocation());
		}
		AffineTransform old = g2d.getTransform();
		super.paintComponent(g);
		g2d.scale(mScale, mScale);
		for (FO fo : mFOs) {
			fo.draw(shr);
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
		int screen = Spike10.DESIRED_SCREEN;
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

		Spike10 s = new Spike10(rect.width,rect.height);
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