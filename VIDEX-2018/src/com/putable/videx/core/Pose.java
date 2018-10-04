package com.putable.videx.core;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Pose {
    public Pose copy(Pose pose) {
        setPAX(pose.getPAX());
        setPAY(pose.getPAY());
        setR(  pose.getR());
        setSX( pose.getSX());
        setSY( pose.getSY());
        setOAX(pose.getOAX());
        setOAY(pose.getOAY());
        return this;
    }
    public Pose() { }
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
    public double getPAX() { return mPAX; }
    public double getPAY() { return mPAY; }
    public double getR() { return mR; }
    public double getSX() { return mSX; }
    public double getSY() { return mSY; }
    public double getOAX() { return mOAX; }
    public double getOAY() { return mOAY; }
    
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
    public void setPAX(double pax) { mPAX = pax; }
    public void setPAY(double pay) { mPAY = pay; }
    public void setR(double r) { mR = r; }
    public void setS(double s) { setSX(s); setSY(s); }
    public void setS(Point2D sxy) { setSX(sxy.getX()); setSY(sxy.getY()); }
    public void setSX(double sx) { mSX = sx; }
    public void setSY(double sy) { mSY = sy; }
    public void setOAX(double oax) { mOAX = oax; }
    public void setOAY(double oay) { mOAY = oay; }

    private double mPAX = 0, mPAY = 0; // Anchor position in parent's frame
    private double mR = 0;             // Rotation around anchor in degrees counterclockwise
    private double mOAX = 0, mOAY = 0; // Anchor position in our frame
    private double mSX = 1.0f, mSY = 1.0f; // Scaling around (mOAX,mOAY) in our frame
        
    @Override
    public String toString() {
        return String.format("(%.1f, %.1f, @%.1f + (%.1f ,%.1f), *%.1f, %.1f)", 
                getPAX(), getPAY(), getR(), 
                getOAX(), getOAY(),
                getSX(), getSY());
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
        at.setTransform(
                sx*cosr,  sx*sinr, 
                sy*-sinr, sy*cosr,
                px*sx*cosr + py*sy*-sinr + ox,
                px*sx*sinr + py*sy*cosr + oy);
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
    
/*
    @Override
    public void configureSelf(ASTObj yourMap, OIOAbleGlobalMap refs) {
        this.setOnum(yourMap.getOnum());
        Double d;
        d = yourMap.checkForDefNumeric("mPAX"); if (d != null) this.mPAX = d;
        d = yourMap.checkForDefNumeric("mPAY"); if (d != null) this.mPAY = d;
        d = yourMap.checkForDefNumeric("mR");   if (d != null) this.mR   = d;
        d = yourMap.checkForDefNumeric("mSX");  if (d != null) this.mSX  = d;
        d = yourMap.checkForDefNumeric("mSY");  if (d != null) this.mSY  = d;
        d = yourMap.checkForDefNumeric("mOAX"); if (d != null) this.mOAX = d;
        d = yourMap.checkForDefNumeric("mOAY"); if (d != null) this.mOAY = d;
                    
    }
*/
    
}
