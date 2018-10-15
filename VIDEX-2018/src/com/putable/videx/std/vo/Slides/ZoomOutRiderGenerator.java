package com.putable.videx.std.vo.Slides;

import com.putable.videx.interfaces.Rider;

public class ZoomOutRiderGenerator implements SlideRiderGenerator {

    private int mOnum = -1;

    @Override
    public Rider generate() {
        return new ZoomOutRider();
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