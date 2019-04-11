package com.putable.videx.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Pose {
    public static Pose make(Point2D p, double r, Point2D s, Point2D o) {
        Pose ret = new Pose();
        ret.setPA(p);
        ret.setR(r);
        ret.setS(s);
        ret.setOA(o);
        return ret;
    }

    private double interp(double tofrac, double from, double to) {
        tofrac = Math.min(1.0, Math.max(0.0, tofrac));
        return tofrac * to + (1 - tofrac) * from;
    }

    /**
     * Modify this to be fromfrac of from + (1-fromfrac) of to
     * 
     * @param from from pose
     * @param to to pose
     * @param tofrac amount of to to use
     */
    public void interpolate(Pose from, Pose to, double tofrac) {

        if (tofrac <= 0)
            this.copy(from);
        else if (tofrac >= 1)
            this.copy(to);
        else {
            setPAX(interp(tofrac, from.getPAX(), to.getPAX()));
            setPAY(interp(tofrac, from.getPAY(), to.getPAY()));
            setR(interp(tofrac, from.getR(), to.getR()));
            setSX(interp(tofrac, from.getSX(), to.getSX()));
            setSY(interp(tofrac, from.getSY(), to.getSY()));
            setOAX(interp(tofrac, from.getOAX(), to.getOAX()));
            setOAY(interp(tofrac, from.getOAY(), to.getOAY()));
        }
    }

    /**
     * Copy the given sourcePose to this
     * @param sourcePose the pose to copy
     * @return this
     */
    public Pose copy(Pose sourcePose) {
        setPAX(sourcePose.getPAX());
        setPAY(sourcePose.getPAY());
        setR(sourcePose.getR());
        setSX(sourcePose.getSX());
        setSY(sourcePose.getSY());
        setOAX(sourcePose.getOAX());
        setOAY(sourcePose.getOAY());
        return this;
    }

    public Pose() {
    }

    public Pose(Pose pose) {
        copy(pose);
    }

    public Point2D getParentAnchor(Point2D xy) {
        if (xy == null)
            xy = new Point2D.Double();
        xy.setLocation(mPAX, mPAY);
        return xy;
    }

    public Point2D getObjectAnchor(Point2D xy) {
        if (xy == null)
            xy = new Point2D.Double();
        xy.setLocation(mOAX, mOAY);
        return xy;
    }

    public double getPAX() {
        return mPAX;
    }

    public double getPAY() {
        return mPAY;
    }

    public double getR() {
        return mR;
    }

    public double getSX() {
        return mSX;
    }

    public double getSY() {
        return mSY;
    }

    public double getOAX() {
        return mOAX;
    }

    public double getOAY() {
        return mOAY;
    }

    public void setPA(Point2D xy) {
        setPAX(xy.getX());
        setPAY(xy.getY());
    }

    public void setOA(Point2D xy) {
        setOAX(xy.getX());
        setOAY(xy.getY());
    }

    public void setBasic(double pax, double pay, double s) {
        setPAX(pax);
        setPAY(pay);
        setS(s);
    }

    public void setPAX(double pax) {
        mPAX = pax;
    }

    public void setPAY(double pay) {
        mPAY = pay;
    }

    public void setR(double r) {
        mR = r;
    }

    public void setS(double s) {
        setSX(s);
        setSY(s);
    }

    public void setS(Point2D sxy) {
        setSX(sxy.getX());
        setSY(sxy.getY());
    }

    public void setSX(double sx) {
        mSX = sx;
    }

    public void setSY(double sy) {
        mSY = sy;
    }

    public void setOAX(double oax) {
        mOAX = oax;
    }

    public void setOAY(double oay) {
        mOAY = oay;
    }

    private double mPAX = 0, mPAY = 0; // Anchor position in parent's frame
    private double mR = 0; // Rotation around anchor in degrees counterclockwise
    private double mOAX = 0, mOAY = 0; // Anchor position in our frame
    private double mSX = 1.0f, mSY = 1.0f; // Scaling around (mOAX,mOAY) in our
                                           // frame

    public String stringify() {
        return ""
                +getPAX()+","
                +getPAY()+","
                +getR()+","
                +getOAX()+","
                +getOAY()+","
                +getSX()+","
                +getSY();
    }
    
    public static Pose destringify(String str) {
        String[] strs = str.split(",");
        if (strs.length != 7) return null;
        try {
            Pose p = new Pose();
            p.mPAX = Double.parseDouble(strs[0]);
            p.mPAY = Double.parseDouble(strs[1]);
            p.mR   = Double.parseDouble(strs[2]);
            p.mOAX = Double.parseDouble(strs[3]);
            p.mOAY = Double.parseDouble(strs[4]);
            p.mSX  = Double.parseDouble(strs[5]);
            p.mSY  = Double.parseDouble(strs[6]);
            return p;
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public String toString() {
        return String.format("(%.1f, %.1f, @%.1f + (%.1f ,%.1f), *%.1f, %.1f)",
                getPAX(), getPAY(), getR(), getOAX(), getOAY(), getSX(),
                getSY());
    }

    public AffineTransform initAffineFromBAKED(AffineTransform at) {
        double cosr = Math.cos(this.getR());
        double sinr = Math.sin(this.getR());
        double sx = this.getSX();
        double sy = this.getSY();
        double ox = this.getOAX();
        double oy = this.getOAY();
        double px = this.getPAX();
        double py = this.getPAY();
        at.setTransform(sx * cosr, sx * sinr, sy * -sinr, sy * cosr,
                px * sx * cosr + py * sy * -sinr + ox,
                px * sx * sinr + py * sy * cosr + oy);
        return at;
    }

    public AffineTransform initAffineFrom(AffineTransform at) {
        at.setToIdentity();
        at.translate(this.getPAX(), this.getPAY());
        at.rotate(this.getR());
        at.scale(this.getSX(), this.getSY());
        at.translate(-this.getOAX(), -this.getOAY());
        return at;
    }

}
