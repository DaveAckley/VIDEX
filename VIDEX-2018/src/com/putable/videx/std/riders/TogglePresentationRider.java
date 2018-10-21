package com.putable.videx.std.riders;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;

public class TogglePresentationRider extends SOSIPoseRider {

    @OIO
    private boolean mRiderActive = false;
    
    {
        this.setDieOnComplete(false);
    }
    private static final int mStartY = 2020;
    private static final int mEndY = 20;
    private static final int[] mYPos = new int[3];
    {
        this.mMaxStep = 15;
        this.mWidth = 4.5;
        mYPos[0] = mStartY;
        mYPos[1] = 150;
        mYPos[2] = mEndY;
    }

    private static final double mStartScale = 0.3;
    private static final double mEndScale = 0.55;
    private static final double mFrontScale = 6.5;

    private static final double[] mScales = new double[] { 
            mStartScale, mFrontScale, mEndScale
    };

    private static final int mStartX = 30;
    private static final int mXOffset = 100;
    private static final int mXForCurrent = 100;

    @OIO
    private boolean mIsPresented = false;
    
    @OIO
    private Pose mPresentationPose = null;

    private Pose getPresentationPose() {
        if (mPresentationPose == null)
            mPresentationPose = new Pose();
        return mPresentationPose;
    }

    @OIO
    private Pose mStashedPose = null;
    
    private Pose getStashedPose() {
        if (mStashedPose == null)
            mStashedPose = new Pose();
        return mStashedPose;
    }
    
    private void togglePresentation() {
        System.out.println("TOGGLERP "+mIsPresented);
        if (mIsPresented) {
            mIsPresented = false;
            Pose stash = getStashedPose();
            this.copyTo(stash);
            this.awaken();
        } else {
            mIsPresented = true;
            Pose pres = getPresentationPose();
            this.copyTo(pres);
            this.awaken();
            StandardVO veh = this.getLastStandardVehicle();
            if (veh != null) 
                veh.requestTop();
        }
    }
    
    @Override
    public void act() {
        if (this.mRiderActive) super.act();
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        VO veh = this.getVehicle();
        if (veh instanceof EventAwareVO) {
            EventAwareVO evo = (EventAwareVO) veh;
            if (!evo.isMouseTarget()) return false;
        }
        if (kei.isKeyTyped('D')) {
            if (veh != null) veh.killVO();
            return true;
        }
        if (kei.isKeyTyped(' ') || kei.isKeyTyped('t')) {
            togglePresentation();
            return true;
        }
        if (kei.isKeyTyped('a')) {
            this.mRiderActive = !this.mRiderActive;
            return true;
        }
        if (kei.isKeyTyped('p')) {
            if (veh != null) {
                Pose pres = getPresentationPose();
                pres.copy(veh.getPose());
                System.out.println("PRES TO "+pres);
            }
            return true;
        }
        if (kei.isKeyTyped('s')) {
            if (veh != null) {
                Pose stash = getStashedPose();
                stash.copy(veh.getPose());
                System.out.println("STASH TO "+stash);
            }
            return true;
        }
        return false;
    }
    
}
