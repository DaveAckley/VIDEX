package com.putable.videx.std.vo.Slides;

import com.putable.videx.core.Pose;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.interfaces.VO;

/**
 * PopupRider switches between invisible and posed, with no tweening
 * @author ackley
 *
 */
public class PopupRider extends BasicSlideRider {
    private static final int mStartY = 2020;
    private static final int mEndY = 20;
    private static final int[] mYPos = new int[3];
    {
        this.mMaxStep = 15;
        this.mWidth = 4.5;
        mYPos[0] = mStartY;
        mYPos[1] = 110;
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

    private VO mVOToPop = null;
    @OIO
    private Pose mPoppedPose = new Pose();
    @OIO
    private Pose mUnpoppedPose = new Pose();
    private Pose mCurrentPose = mUnpoppedPose;
    private void doPop(VO toPop, boolean popped) {
    	mCurrentPose = popped?  mPoppedPose : mUnpoppedPose;
    	toPop.getPose().copy(mCurrentPose);
    	
    }
    
    @Override
    public Pose getDestination(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
    	return mCurrentPose;
/*
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
*/
    }

	@Override
	public void awaken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void observe(VO vo) {
		mVOToPop = vo;
		
	}

	@Override
	public int react(VO vo) {
		return Rider.REACT_REOBSERVE;
	}

	@Override
	public void act() {
		if (mVOToPop == null) return;
		if (mVOToPop instanceof BasicSlideAudioClip) { ///GAAAAAACK XXX
			BasicSlideAudioClip bsac = (BasicSlideAudioClip) mVOToPop;
			doPop(bsac,bsac.isClipPlaying());
				
		}
		
	}
    
}
