package com.putable.hyperspace.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

/* FrameDemo.java requires no other files. */
public class FOGU10 {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jp = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
        	public void paintComponent(Graphics g) {
        		super.paintComponent(g);
        		Rectangle rect = this.getBounds();
        		Graphics2D g2d = (Graphics2D) g;
        		g2d.drawLine(rect.x,rect.y,rect.width,rect.height);
        	}
        };
        jp.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("jp resized "+jp.getBounds());
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        jp.setBackground(Color.black);
        jp.setForeground(Color.yellow);
        //emptyLabel.setPreferredSize(new Dimension(175, 100));
        frame.getContentPane().add(jp, BorderLayout.CENTER);

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
