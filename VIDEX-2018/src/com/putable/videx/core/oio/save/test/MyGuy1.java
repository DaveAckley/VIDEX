package com.putable.videx.core.oio.save.test;

import java.util.Arrays;
import java.util.LinkedList;

import com.putable.videx.core.oio.OIO;

public class MyGuy1 extends Base { 
    @OIO
    private String mStrs = ""+getOnum()+"-"+getOnum();

    @OIO
    private int mFoo = 3*getOnum();

    @OIO
    private Base mSub = new MyGuy2();
    
    @OIO(owned=false)
    private Base mOther = mSub;
    
    @OIO
    private int[] mHogmonics = { 2, 7, 22, 42, 99, -8};
    
    @OIO(inline=false,extension="html")
    private String mHTML = "ho ho ho ho hi";

    @OIO
    private LinkedList<Integer> mData =  new LinkedList<Integer>(Arrays.asList(1,2,3,4,5,9));
}