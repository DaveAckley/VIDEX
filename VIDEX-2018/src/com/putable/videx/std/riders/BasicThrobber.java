package com.putable.videx.std.riders;

import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardRider;
import com.putable.videx.interfaces.VO;

public class BasicThrobber extends StandardRider {
    private double mThrobExcursionPercent = 1;
    private int mThrobMaxSteps = 50;
    private int mThrobCurrentStep;
    private VO mThrobTarget;
    private double mBaseScaleX;
    private double mBaseScaleY;
    private double mLastScaleX;
    private double mLastScaleY;

    @Override
    public void awaken() {
        mThrobCurrentStep = 0;
    }

    @Override
    public void observe(VO vo) {
        mThrobTarget = vo;
        mLastScaleX = mBaseScaleX = mThrobTarget.getPose().getSX();
        mLastScaleY = mBaseScaleY = mThrobTarget.getPose().getSY();
    }

    @Override
    public int react(VO vo) {
        if (killed()) return REACT_DIE;
        if (vo != mThrobTarget) return REACT_REOBSERVE;
        Pose pose = vo.getPose();
        if (pose.getSX() != mLastScaleX || pose.getSY() != mLastScaleY)
            return REACT_REOBSERVE;
        return REACT_CONTINUE;
    }

    @Override
    public void act() {
        if (++mThrobCurrentStep >= mThrobMaxSteps)
            mThrobCurrentStep = 0;
        double frac = ((double) mThrobCurrentStep) / mThrobMaxSteps * Math.PI * 2;
        double pct = (100.0 + Math.sin(frac) * mThrobExcursionPercent)/100.0;
        mLastScaleX = mBaseScaleX * pct;
        mLastScaleY = mBaseScaleY * pct;
        Pose pose = mThrobTarget.getPose();
        pose.setSX(mLastScaleX);
        pose.setSY(mLastScaleY);
    }

}
