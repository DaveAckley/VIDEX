package com.putable.videx.std.vo.Slides;

import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.RiderGenerator;
import com.putable.videx.std.riders.SOSIPoseRider;

public class SOSIPoseRiderGenerator implements RiderGenerator {
    private int mOnum = -1;

    @Override
    public Rider generate() {
        return new SOSIPoseRider();
    }

    @Override
    public int getOnum() {
        return mOnum;
    }

    @Override
    public void setOnum(int onum) {
        mOnum = onum;
    }
}