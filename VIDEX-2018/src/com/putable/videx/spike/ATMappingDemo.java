package com.putable.videx.spike;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class ATMappingDemo extends JFrame {

    private static final long serialVersionUID = 1L;

    private void test(AffineTransform at) {
        Point2D a1 = new Point2D.Double(0,0);
        Point2D a2 = new Point2D.Double(100,200);
        Point2D a3 = new Point2D.Double(-100,10);
        Point2D d1,d2,d3;
        d1 = at.transform(a1, null);
        d2 = at.transform(a2, null);
        d3 = at.transform(a3, null);
        System.out.println(at);
        System.out.println(a1+" -> "+d1);
        System.out.println(a2+" -> "+d2);        
        System.out.println(a3+" -> "+d3);        
    }
    void run() {
        AffineTransform at = new AffineTransform();
        test(at);
        
        at.scale(2, 2);
        at.translate(20, 30);
        test(at);
        AffineTransform back = null;
        try {
            back = at.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        test(back);
    }
    
    public static void main(String[] args) {
        new ATMappingDemo().run();

    }

}
