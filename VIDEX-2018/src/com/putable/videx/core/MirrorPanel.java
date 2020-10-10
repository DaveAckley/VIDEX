package com.putable.videx.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

class MirrorPanel extends JPanel {
    public MirrorPanel(LayoutManager lm) {
        super(lm);

        // To handle key events
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                dispatchKeyEvent(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                dispatchKeyEvent(e);
            }
            @Override
            public void keyTyped(KeyEvent e) {
                dispatchKeyEvent(e);
            }

            private void dispatchKeyEvent(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_ESCAPE:   // ESC to quit
                    System.exit(0);
                    break;
                }
                switch(e.getKeyChar()) {
                case 's': mScale = 0.95*mScale; break;
                case 'S': mScale = 1.05*mScale; break;
                case 'x': mXOffset -= 1; break;
                case 'X': mXOffset += 1; break;
                case 'y': mYOffset -= 1; break;
                case 'Y': mYOffset += 1; break;
                default:
                    //mStagePanel.dispatchKeyEvent(e);
                    System.out.println("MIRPGOT:"+e);
                    break;
                }
                System.out.println("S="+mScale+" X="+mXOffset+" Y="+mYOffset);
                MirrorPanel.this.repaint();
            }
            
        });

        this.setFocusable(true);  // To receive key event
    }
    private double mScale = 2.62;
    private double mXOffset = -180;
    private double mYOffset = -64;
    @Override
    public void paintChildren(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform old = g2d.getTransform();
        double s = mScale;
        double h = this.getHeight()/s;
        double w = this.getWidth()/s;
        g2d.scale(-s, s);
        g2d.translate(-w/2+mXOffset,-h/2+mYOffset);
        //g2d.rotate(Math.toRadians(180));
        //g2d.translate(h/2,-w/2);
        super.paintChildren(g2d);
        g2d.setTransform(old);
    }
}