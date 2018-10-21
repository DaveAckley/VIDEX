package com.putable.videx.std.riders;

import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardRider;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;

public class SOSIPoseRider extends StandardRider {

    public SOSIPoseRider(Pose destination) {
        copyTo(destination);
    }

    public SOSIPoseRider() {
    }

    /**
     * Copy dest into the the SOSPoseRider's destination.
     * @param dest Pose to head for
     */
    public void copyTo(Pose dest) {
        mTo.copy(dest);
    }

    /**
     * Copy source into the the SOSPoseRider's starting point.
     * @param source Pose to start from
     */
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

    @OIO(owned = false)
    private StandardVO mLastStandardVehicle = null;

    public VO getVehicle() {
        return mVehicle; // Only valid after observe()
    }

    public StandardVO getLastStandardVehicle() {
        return mLastStandardVehicle;
    }
    
    @Override
    public void awaken() {
        mVehicle = null;
    }

    @Override
    public void observe(VO vo) {
        if (mVehicle == null) {
            if (vo instanceof StandardVO) {
                mLastStandardVehicle = (StandardVO) vo;
            }
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

    public void setCurrentStep(int step) {
        mCurrentStep = step;
    }
    
    public int getCurrentStep() {
        return mCurrentStep;
    }
    
    @Override
    public void act() {
        double fracdone = this.getTraveledFraction(mCurrentStep);
        mVehicle.getPose().interpolate(mFrom, mTo, fracdone);
        ++mCurrentStep;
    }

}
