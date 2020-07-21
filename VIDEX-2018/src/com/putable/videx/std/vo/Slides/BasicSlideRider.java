package com.putable.videx.std.vo.Slides;

import com.putable.videx.core.Pose;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.interfaces.VO;
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
    
    /**
     * Compute the destination for this slide given that the presentation is 
     * currently on slide# currentNum, and this is slide# yourNum of totalSlides
     * @param sd the deck
     * @param currentNum the slide we're showing
     * @param yourNum this slide's number
     * @param totalSlides the size of the deck
     * @return the Pose that this slide should be SOSIPoseRiding toward
     */
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
            VO vo = this.getVehicle();
            if (vo instanceof BasicSlide) {
                BasicSlide bs = (BasicSlide) vo;
                String notes = bs.getNotesStringIfAny();
                if (notes == null) notes = "";
                else {
                    System.out.println("NOTES NOTES NOTES DO SOMETHING");
                }
            }
        }
    }

}
