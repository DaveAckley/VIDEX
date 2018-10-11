package com.putable.videx.std.vo.Slides;

import com.putable.videx.core.Pose;
import com.putable.videx.interfaces.SlideDeck;

public class ThreeStopRider extends BasicSlideRider {
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

    @Override
    public Pose getDestination(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        int delta = currentNum-yourNum;
        int index;
        if (delta < 0) index = 0;
        else if (delta > 0) index = 2;
        else index = 1;
        Pose p = new Pose();
        p.setPAY(mYPos[index]);
        int homex = yourNum*mXOffset + mStartX;
        int tox = 0;
        switch(index) {
        case 0: case 2: tox = homex; break;
        case 1: tox = mXForCurrent; break;
        }
        p.setPAX(tox);
        p.setS(mScales[index]);
        return p;
    }
    
}
