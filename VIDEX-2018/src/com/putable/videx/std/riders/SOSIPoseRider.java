package com.putable.videx.std.riders;

import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardRider;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;

public class SOSIPoseRider extends StandardRider {

    public SOSIPoseRider(Pose destination) {
        copyTo(destination);
    }

    public SOSIPoseRider() {
    }

    public void copyTo(Pose dest) {
        mTo.copy(dest);
    }

    public void copyFrom(Pose source) {
        mFrom.copy(source);
    }

    @OIO
    private Pose mTo = new Pose();
    @OIO
    private Pose mFrom = new Pose();
    @OIO
    protected double mWidth = 4.0;
    @OIO
    protected double mTemperature = 1.0;
    @OIO
    protected int mMaxStep = 12;
    @OIO
    private int mCurrentStep = 0;
    @OIO
    private boolean mDieOnComplete = true;

    public boolean isDieOnComplete() {
        return mDieOnComplete;
    }

    public void setDieOnComplete(boolean mDieOnComplete) {
        this.mDieOnComplete = mDieOnComplete;
    }

    private double getTraveledFraction(int steps) {
        if (steps <= 0)
            return 0.0;
        if (steps >= mMaxStep)
            return 1.0;
        double frac = (double) steps / mMaxStep;
        double nonlin = 1 / (1
                + Math.pow(Math.E, -mWidth * (2 * frac - 1) / mTemperature));
        return nonlin;
    }

    @OIO(owned = false)
    private VO mVehicle = null;

    @Override
    public void awaken() {
        mVehicle = null;
    }

    @Override
    public void observe(VO vo) {
        if (mVehicle == null) {
            mVehicle = vo;
            mCurrentStep = 0;
            mFrom = new Pose(vo.getPose());
        }
    }

    @Override
    public int react(VO vo) {
        if (vo != mVehicle) {
            return REACT_REAWAKEN;
        }
        if (mCurrentStep > mMaxStep + 1) {
            if (mDieOnComplete)
                return REACT_DIE;
            mCurrentStep = mMaxStep;
        }
        return REACT_CONTINUE;
    }

    @Override
    public void act() {
        double fracdone = this.getTraveledFraction(mCurrentStep);
        mVehicle.getPose().interpolate(mFrom, mTo, fracdone);
        ++mCurrentStep;
    }

}
