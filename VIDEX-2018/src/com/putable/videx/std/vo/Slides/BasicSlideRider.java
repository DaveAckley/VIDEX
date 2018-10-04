package com.putable.videx.std.vo.Slides;

import com.putable.videx.core.Pose;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.std.riders.SOSIPoseRider;

public class BasicSlideRider extends SOSIPoseRider implements SlideRider {

    @OIO
    private int mHiddenXStart = 50;

    @OIO
    private int mHiddenXIncrement = 100;

    @OIO
    private double mHiddenScale = 0.2;

    {
        this.setDieOnComplete(false);
    }

    @OIO
    private int mLastCurrentNum = -1;
    
    public Pose getDestination(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        Pose dest = new Pose();
        if (currentNum == yourNum) {
            dest.setPAX(-0.005);
            dest.setPAY(400);
            dest.setS(7);
        } else {
            dest.setPAX(mHiddenXStart+yourNum*mHiddenXIncrement);
            dest.setPAY(2000);
            dest.setS(mHiddenScale);
        }
        return dest;
    }

    @Override
    public void deckStatus(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        Pose dest = getDestination(sd,currentNum,yourNum,totalSlides);
        this.copyTo(dest);
        if (currentNum!=mLastCurrentNum) {
            this.awaken();
            mLastCurrentNum = currentNum;
        }
    }

}
