package com.putable.videx.std.vo.Slides;

import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.SlideDeck;

public interface SlideRider extends Rider {

    /**
     * Handle deckstatus reports
     * @param sd
     * @param currentNum
     * @param yourNum
     * @param totalSlides
     */
    public void deckStatus(SlideDeck sd, int currentNum, int yourNum, int totalSlides) ;
        

}
