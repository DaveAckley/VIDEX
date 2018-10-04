package com.putable.videx.core;

import java.awt.geom.Point2D;

public class TriggerPoint extends StandaloneOIOAble {
    private int mOnum = -1;
    private double mTriggerX;
    private double mTriggerY;
    private boolean mKilled = false;
    private boolean mEnabled = true;

    @Override
    public String toString() {
        return String.format("TP%s%s[%.1f,%.1f]",
                (mEnabled?"e":""),
                (mKilled?"k":""),
                mTriggerX,
                mTriggerY);
    }
    
    public Point2D getTriggerPoint(Point2D pt) {
        if (pt == null) pt = new Point2D.Double();
        pt.setLocation(mTriggerX, mTriggerY);
        return pt;
    }
    
    public TriggerPoint(double x, double y) {
        mTriggerX = x;
        mTriggerY = y;
    }

    public double getTriggerX() {
        return mTriggerX;
    }

    public void setTriggerX(double mTriggerX) {
        this.mTriggerX = mTriggerX;
    }

    public double getTriggerY() {
        return mTriggerY;
    }

    public void setTriggerY(double mTriggerY) {
        this.mTriggerY = mTriggerY;
    }

    public boolean isKilled() {
        return mKilled;
    }

    public void kill() {
        this.mKilled = true;
        this.mEnabled = false;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean mEnabled) {
        this.mEnabled = mEnabled;
    }

    @Override
    public int getOnum() {
        return this.mOnum;
    }
/*
    @Override
    public void configureSelf(ASTObj yourMap, OIOAbleGlobalMap refs) {
        throw new UnsupportedOperationException("WRITE ME");
        
    }
*/
}
