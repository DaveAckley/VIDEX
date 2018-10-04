package com.putable.videx.core.events;

import java.awt.event.KeyEvent;

public class KeyboardEventInfo extends IOEvent {
    private final KeyEvent mKeyEvent;

    public KeyEvent getKeyEvent() {
        return mKeyEvent;
    }

    public KeyboardEventInfo(KeyEvent ke) {
        mKeyEvent = ke;
    }
}
