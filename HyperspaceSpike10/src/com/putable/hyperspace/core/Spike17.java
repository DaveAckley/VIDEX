package com.putable.hyperspace.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Spike17 {
	private static final int DESIRED_X_POS = 0;
	private static final int DESIRED_Y_POS = 0;
	private static final int DESIRED_WIDTH = 1920*2;
	private static final int DESIRED_HEIGHT= 1080*2;
	private static final int DESIRED_PANEL_WIDTH = 2*DESIRED_WIDTH/3;
	
	private static class SpikeFrame extends JFrame implements KeyListener {
		HIP mHip;

		private static final long serialVersionUID = 1L;

		public SpikeFrame(String s) {
			super(s);
			addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			this.setLayout(new BorderLayout());
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			this.mHip.dispatchKeyEvent(arg0);
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			this.mHip.dispatchKeyEvent(arg0);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar()==KeyEvent.VK_ESCAPE || e.getKeyChar()=='Q') // NB: Shift-q
				System.exit(0);
			this.mHip.dispatchKeyEvent(e);
		}
	}
	
	private static void createAndShowGUI() {
		//Create and set up the window.
		SpikeFrame frame = new SpikeFrame("SPIKE10");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		HIP hip = new HIPSpike13();
		frame.mHip = hip;

		frame.setLocation(Spike17.DESIRED_X_POS,Spike17.DESIRED_Y_POS);
		frame.setSize(Spike17.DESIRED_WIDTH,Spike17.DESIRED_HEIGHT);

		// Go for fullscreen
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		frame.setUndecorated(true);
		frame.setVisible(true);
		
		hip.buildDiagram();
		JPanel display = hip.getJPanel();
		display.setPreferredSize(new Dimension(Spike17.DESIRED_PANEL_WIDTH,Spike17.DESIRED_HEIGHT));
		Container cont = frame.getContentPane();
		cont.setBackground(Color.black);
		cont.add(display,BorderLayout.WEST);
		frame.pack();
		frame.setVisible(true);
		
		//Start the update timer
	}
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
