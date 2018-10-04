package com.putable.videx.core.oio.save.test;

import com.putable.videx.core.oio.OIO;

public class MyGuy2 extends Base { 
    @OIO(inline=false,extension=".html")
    private String mHTML = "he he he he he her";
    @OIO
    private float[] mCloneBombs = { 1.2f, 3.4f, -5.6f };
    @OIO
    private double mFrac = 1.0/getOnum();
    @OIO
    private boolean mFlag = mFrac > .3;
}