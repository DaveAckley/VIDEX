package com.putable.videx.demotest11;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class NewStandAloneSpike {
    class Pose {
        double mPAX, mPAY; // Anchor position in parent's frame
        double mR;         // Rotation around anchor, in degrees counterclockwise
        double mSX, mSY;   // Scaling around anchor, in x and y
        double mOAX, mOAY; // Anchor position in our frame
        Pose mParent = null;
        AffineTransform mAF = new AffineTransform();

        public void set(double pax, double pay, double r, double sx, double sy,
                double oax, double oay) {
            mPAX = pax;
            mPAY = pay;
            mR = r;
            mSX = sx;
            mSY = sy;
            mOAX = oax;
            mOAY = oay;
            mAF.setToIdentity();
            mAF.translate(mPAX, mPAY);
            mAF.rotate(mR * Math.PI / 180);
            mAF.scale(mSX, mSY);
            mAF.translate(-mOAX, -mOAY);
        }

        public void setParent(Pose parent) {
            mParent = parent;
        }

        public Point2D mapThroughPose(Point2D from, Point2D to) {
            if (to == null)
                to = new Point2D.Double();
            to = mAF.transform(from, to);
            System.out.println("From " + from + " To " + to + " Via " + this
                    + " == " + mAF);
            return to;
        }

        public Point2D mapToStage(Point2D from) {
            Point2D to = mapThroughPose(from, null);
            return (mParent != null) ? mParent.mapToStage(to) : to;
        }

        public AffineTransform getCompositeTransform(AffineTransform af) {
            if (mParent != null) af = mParent.getCompositeTransform(af);
            if (af == null) af = new AffineTransform();
            af.concatenate(mAF);
            return af;
        }

        @Override
        public String toString() {
            return String.format(
                    "Pose[%.1f, %.1f @ %.1f * %.1f, %.1f + %.1f, %.1f]", mPAX,
                    mPAY, mR, mSX, mSY, mOAX, mOAY);
        }
    }

    public void showMapping(Pose from, double x, double y) {
        Point2D pt1 = new Point2D.Double(x, y);
        Point2D to = from.mapToStage(pt1);
        double dist = pt1.distance(to);
        System.out.println("Local: " + pt1 + " -> Stage: " + to + " Dist: "
                + dist + "\n");

    }

    public void demo2() {
        Pose p1 = new Pose();
        Pose p2 = new Pose();
        p2.setParent(p1);
        p1.set(2, 0, -90, 0.5, 0.5, 0, 0); // p1 on stage (-8,13) -> (13,8) ->
                                           // (6.5,4) -> (8.5,4)
        p2.set(0, 7, 90, 2, 2, 0, 0); // p2 on p1 (3,4) -> (-4,3) -> (-8,6) ->
                                      // (-8,13)
        //showMapping(p2, 3, 4); // map (3,4) in p2 to stage via p1

        p1.set(1, 2, -90, 1.0, 1.0, 0, 0);
        p2.set(0, 0, 90, 2, 2, 2, 1);
        showMapping(p2, 3, 4);
        //showMapping(p2, 2.9, 3.9);
        //showMapping(p2, 3.1, 4.1);

        AffineTransform af = p2.getCompositeTransform(null);
        Point2D pta = new Point2D.Double(3, 4);
        Point2D ptb = af.transform(pta, null);
        Point2D pti = null;
        try {
            pti = af.inverseTransform(ptb, null);
        } catch (NoninvertibleTransformException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("NET "+ af + " in " + pta + " out " + ptb + " back " + pti);
    }

    public static void main(String[] args) {
        NewStandAloneSpike ns = new NewStandAloneSpike();
        ns.demo2();
    }
}
