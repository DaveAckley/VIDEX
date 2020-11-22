package com.putable.hyperspace.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Spike15 {
	private static final int DESIRED_SCREEN = 1;
	private static class SpikeFrame extends JFrame implements KeyListener {
		private static final long serialVersionUID = 1L;

		public SpikeFrame(String s) {
			super(s);
			addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar()==KeyEvent.VK_ESCAPE || e.getKeyChar()=='Q') // NB: Shift-q
				System.exit(0);
		}
	}
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new SpikeFrame("SPIKE10");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		System.out.println("tk.gSS = "+dim);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		for (GraphicsDevice gc : gd) {
			DisplayMode dm = gc.getDisplayMode();
			/*
			DisplayMode[] dms = gc.getDisplayModes();
			for (DisplayMode dmm : dms) {
				System.out.println("DMMMMSS "+dmm.getWidth()+" "+dmm.getHeight());
			}
*/
			System.out.println(gc + " DM " + dm.getWidth() + ", " + dm.getHeight());
			System.out.println(gc + " defb " + gc.getDefaultConfiguration().getBounds());
		}
		GraphicsDevice thegd;
		int screen = Spike15.DESIRED_SCREEN;
		if(screen > -1 && screen < gd.length ) {
			thegd = gd[screen];
		} else if( gd.length > 0 ) {
			thegd = gd[0];
			frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, frame.getY());
		} else {
			throw new RuntimeException( "No Screens Found" );
		}
		/*
		GraphicsConfiguration [] gcs  = thegd.getConfigurations();
		for (GraphicsConfiguration gc : gcs) {
			System.out.println(gc + " bd " + gc.getBounds());
		}
*/		
		Rectangle rect = thegd.getDefaultConfiguration().getBounds();
		System.out.println(thegd + " rect " + rect);
		frame.setLocation(rect.x, frame.getY());
		frame.setSize(rect.width,rect.height);

		HIP hip = new HIPSpike11();
		hip.getJPanel().setPreferredSize(new Dimension(2*rect.width/3,rect.height));
		frame.getContentPane().add(hip.getJPanel(),BorderLayout.WEST); 
		frame.getContentPane().add(new BlackPanel(),BorderLayout.CENTER);
		hip.configure(rect);
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
