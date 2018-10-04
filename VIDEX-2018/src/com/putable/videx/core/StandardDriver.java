package com.putable.videx.core;

import com.putable.videx.interfaces.Driver;
import com.putable.videx.interfaces.OIOAble;

public abstract class StandardDriver implements Driver, OIOAble {

    private int mOnum = -1;
    
    public void setOnum(int onum) {
        if (onum <= 0) throw new IllegalArgumentException();
        if (mOnum > 0) throw new IllegalStateException();
        mOnum = onum;
    }
    
    @Override
    public int getOnum() {
        return 0;
    }

}
