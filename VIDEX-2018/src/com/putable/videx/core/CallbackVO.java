package com.putable.videx.core;

import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;

public abstract class CallbackVO extends EventAwareVO {
    @OIO(owned=false)
    private StandardVO mCallbackVO = null;

    public void setCallback(StandardVO callback) {
        mCallbackVO = callback;
    }
    
    public StandardVO getCallback() {
        return mCallbackVO;
    }

    @OIO
    private boolean mOneShot = true;
    
    public void setOneShot(boolean oneshot) {
        mOneShot = oneshot;
    }

    public boolean isOneShot() {
        return mOneShot;
    }
    
    @OIO
    private String mStringLoad = "";
    
    public void setStringLoad(String load) {
        if (load == null) throw new IllegalArgumentException();
        mStringLoad = load;
    }
    
    public String getStringLoad() {
        return mStringLoad;
    }

    public CallbackVO() { this(null); }

    public CallbackVO(StandardVO callback) {
        this.setCallback(callback);
    }

    /**
     * Determine if this VO's callback should be invoked on sei
     * @param sei
     * @return true iff the callback should be called to handle sei
     */
    public abstract boolean applicableEvent(SpecialEventInfo sei);
    
    @Override
    public boolean handleSpecialEvent(SpecialEventInfo sei) {
        if (this.mCallbackVO != null && applicableEvent(sei)) {
            mCallbackVO.handleSpecialEventHere(sei);
            if (mOneShot) this.killVO();
            return true;
        }
        return false;
    }

}
