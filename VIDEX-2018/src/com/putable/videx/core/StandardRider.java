package com.putable.videx.core;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.Rider;

public abstract class StandardRider implements Rider, OIOAble {

    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        return false; // By default we do not deal with the keyboard
    }

    private int mOnum = -1;
    @OIO
    private boolean mKilled = false;
    
    @Override 
    public void die() {
        mKilled = true;
    }

    public boolean killed() { return mKilled; }
    
    @Override
    public void setOnum(int onum) {
        if (onum <= 0) throw new IllegalArgumentException();
        if (mOnum > 0) throw new IllegalStateException();
        mOnum = onum;
    }
    
    @Override
    public int getOnum() {
        return mOnum;
    }

}
