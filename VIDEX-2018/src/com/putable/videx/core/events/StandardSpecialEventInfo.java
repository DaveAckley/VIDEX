package com.putable.videx.core.events;

public abstract class StandardSpecialEventInfo extends SpecialEventInfo {

    private String mStringLoad = "";
    
    @Override
    public String getStringLoad() {
        return mStringLoad;
    }
    
    public void setStringLoad(String stringload) {
        if (stringload == null) throw new IllegalArgumentException();
        mStringLoad = stringload;
    }

}
