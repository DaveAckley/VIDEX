package com.putable.videx.core.oio.save.test;

import com.putable.videx.std.vo.Slides.BasicSlide;

public class Base extends BasicSlide {

    private int mOnum = OIOTestLoad.nextOnum();
    private String mName = null;
    @Override
    public int getOnum() {
        return mOnum;
    }
    /*
    @Override
    public String getName() {
        return mName;
    }
    */
    public void setName(String name) { mName = name; }
}