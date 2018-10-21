package com.putable.videx.std.vo.Slides;

import java.awt.geom.Point2D;

import com.putable.videx.core.Pose;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.SlideDeck;

public class ZoomOutSlideRider extends BasicSlideRider {
    private static final double DEFAULT_START_SCALE = 4.0;
    private static final double DEFAULT_END_SCALE = .05;

    @OIO
    private Pose mStartPose =
        Pose.make(new Point2D.Double(1000,2000), 
                0.0,
                new Point2D.Double(DEFAULT_START_SCALE,DEFAULT_START_SCALE), 
                new Point2D.Double(0,0));
    @OIO
    private Pose mEndPose =
        Pose.make(new Point2D.Double(1000,2000), 
                0.0,
                new Point2D.Double(DEFAULT_END_SCALE,DEFAULT_END_SCALE), 
                new Point2D.Double(0,0));

    {
        // protected in SOSIPoseRider
        this.mMaxStep = 20;
    }
    
    @OIO
    private int mHalfSlidesPerZoom = 2;

    @Override
    public Pose getDestination(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        // We want multiple slides moving at once, in general, so we need
        // to figure out interim destinations for slides
        Pose p = new Pose();
        int delta = currentNum-yourNum;
        int index;
        if (delta < -mHalfSlidesPerZoom) {
            // stopped at start
            p.copy(mStartPose);
        } else if (delta >= mHalfSlidesPerZoom) {
            p.copy(mEndPose);
        } else {
            int from = delta + mHalfSlidesPerZoom;
            double destfrac = from/(2.0*mHalfSlidesPerZoom); // 0.0..1.0
            p.interpolate(mStartPose, mEndPose, destfrac);
        }

        return p;
    }
    
}
