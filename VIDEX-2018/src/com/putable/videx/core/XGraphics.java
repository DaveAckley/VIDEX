package com.putable.videx.core;

import java.awt.Graphics2D;

public class XGraphics {

    /**
     * Draw an arrow line between two points. Based on code by phibao37 at
     * https://stackoverflow
     * .com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java
     * 
     * @param g
     *            the graphics component.
     * @param x1
     *            x-position of first point.
     * @param y1
     *            y-position of first point.
     * @param x2
     *            x-position of second point.
     * @param y2
     *            y-position of second point.
     * @param d
     *            the width of the arrow.
     * @param h
     *            the height of the arrow.
     */
    public static void drawArrowLine(Graphics2D g, double x1, double y1,
            double x2, double y2, double d, double h, boolean arrow1,
            boolean arrow2) {

        if (arrow1 || arrow2) {
            double dx = x2 - x1, dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy);
            double indent = d / 2;
            double sin = dy / D, cos = dx / D;

            if (arrow1)
                drawArrowHeadForLine(g, x1, y1, x2, y2, d, h);
            if (arrow2)
                drawArrowHeadForLine(g, x2, y2, x1, y1, d, h);

            if (arrow2) {
                x1 += indent * cos;
                y1 += indent * sin;
            }
            if (arrow1) {
                x2 -= indent * cos;
                y2 -= indent * sin;
            }
        }
        g.drawLine((int) (x1 + 0.), (int) (y1 + 0.), (int) (x2 + 0.), (int) (y2 + 0.));
    }

    public static void drawArrowHeadForLine(Graphics2D g, double x1, double y1,
            double x2, double y2, double d, double h) {
        double dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = { (int) (x2 + 0.), (int) (xm + 0.), (int) (xn + 0.) };
        int[] ypoints = { (int) (y2 + 0.), (int) (ym + 0.), (int) (yn + 0.) };

        g.fillPolygon(xpoints, ypoints, 3);
    }
}
