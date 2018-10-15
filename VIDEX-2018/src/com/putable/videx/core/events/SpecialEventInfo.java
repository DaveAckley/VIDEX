package com.putable.videx.core.events;

public abstract class SpecialEventInfo extends IOEvent {
    // Override if you need multiple types from a single class
    public String getType() {
        return this.getClass().getName();
    }

    public abstract Object getValue();
}
