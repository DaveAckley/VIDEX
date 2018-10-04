package com.putable.videx.std.riders;

import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardRider;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;

public class BarrelRollRider extends StandardRider {

    public BarrelRollRider(Pose destination) {
        mTo.copy(destination);
    }
    @OIO
    private Pose mTo = new Pose();
    @OIO
    private Pose mFrom = new Pose();
    @OIO
    private double mWidth = 4.0;
    @OIO
    private double mTemperature = 1.0;
    @OIO
    private int mMaxStep = 12;
    @OIO
    private int mCurrentStep = 0;

    private double getTraveledFraction(int steps) {
        if (steps <= 0) return 0.0;
        if (steps >= mMaxStep) return 1.0;
        double frac = (double) steps / mMaxStep;
        return 1/(1+Math.pow(Math.E, -mWidth*(2*frac-1)/mTemperature));
    }
    private double interp(double frac, double from, double to) {
        return frac*to+(1-frac)*from;
    }
    @OIO(owned=false)
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
        if (vo != mVehicle) return REACT_REAWAKEN;
        if (mCurrentStep > mMaxStep+1) return REACT_DIE;
        return REACT_CONTINUE;
    }

    @Override
    public void act() {
        double d = this.getTraveledFraction(mCurrentStep);
        Pose p = mVehicle.getPose();
        p.setPAX(interp(d,mFrom.getPAX(),mTo.getPAX()));
        p.setPAY(interp(d,mFrom.getPAY(),mTo.getPAY()));
        p.setR  (interp(d,mFrom.getR(),  mTo.getR()  ));
        p.setSX (interp(d,mFrom.getSX(), mTo.getSX() ));
        p.setSY (interp(d,mFrom.getSY(), mTo.getSY() ));
        p.setOAX(interp(d,mFrom.getOAX(),mTo.getOAX()));
        p.setOAY(interp(d,mFrom.getOAY(),mTo.getOAY()));
        ++mCurrentStep;
    }

}
