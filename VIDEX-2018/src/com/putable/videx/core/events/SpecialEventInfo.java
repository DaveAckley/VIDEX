package com.putable.videx.core.events;

public abstract class SpecialEventInfo extends IOEvent {
    /**
     * SpecialEventInfo types, by default, equal the class name, but this can be
     * overidden if a single class needs to support need multiple types.
     * (Although note that 'instanceof'-based SpecialEventInfo processing will
     * generally not use {@link #getType()} at all, urgh.)
     * 
     * @return
     */
    public String getType() {
        return this.getClass().getName();
    }

    /**
     * Access the information payload for this special event info, whatever you
     * conceive that to be.
     * 
     * @return
     */
    public abstract Object getValue();
}
