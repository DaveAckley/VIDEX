package com.putable.videx.core;

import com.putable.videx.interfaces.OIOAble;

public class StandaloneOIOAble implements OIOAble {
    private int mOnum = -1;
    @Override
    public int getOnum() {
        return mOnum;
    }

    @Override
    public void setOnum(int onum) {
        if (onum <= 0) throw new IllegalArgumentException();
        if (mOnum > 0) throw new IllegalStateException();
        this.mOnum = onum;
    }

}
