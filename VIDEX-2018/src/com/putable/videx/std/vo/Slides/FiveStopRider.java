package com.putable.videx.std.vo.Slides;

import com.putable.videx.core.Pose;
import com.putable.videx.interfaces.SlideDeck;

public class FiveStopRider extends BasicSlideRider {
    private static final int mStartY = 2020;
    private static final int mEndY = 20;
    private static final int[] mYPos = new int[5];
    {
        this.mMaxStep = 30;
        this.mWidth = 4.5;
        mYPos[0] = mStartY;
        mYPos[1] = 1430;
        mYPos[2] = 350;
        mYPos[3] =  30;
        mYPos[4] = mEndY;
    }

    private static final double mEndScale = 0.3;
    private static final double mOnDeckScale = 4.1;
    private static final double mFrontScale = 8;

    private static final double[] mScales = new double[] { 
            mEndScale, mOnDeckScale, mFrontScale, mOnDeckScale, mEndScale
    };

    private static final int mStartX = 50;
    private static final int mXOffset = 20;
    private static final int mXForCurrent = 80;

    @Override
    public Pose getDestination(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        int delta = currentNum-yourNum;
        int index;
        if (delta < -1) index = 0;
        else if (delta > 1) index = 4;
        else index = delta + 2; // 1..3
        Pose p = new Pose();
        p.setPAY(mYPos[index]);
        int homex = yourNum*mXOffset + mStartX;
        int tox = 0;
        switch(index) {
        case 0: case 4: tox = homex; break;
        case 1: case 3: tox = (homex + mXForCurrent) / 2; break;
        case 2: tox = mXForCurrent; break;
        }
        p.setPAX(tox);
        p.setS(mScales[index]);
        return p;
    }
    
}
