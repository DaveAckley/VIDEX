package com.putable.videx.core.events;

import java.awt.event.KeyEvent;

public class KeyboardEventInfo extends IOEvent {
    private final KeyEvent mKeyEvent;

    public boolean isKeyTyped(char ch) {
        KeyEvent ke = getKeyEvent();
        if (ke == null) throw new IllegalStateException();
        if (ke.getID() != ke.KEY_TYPED) return false;
        char c = ke.getKeyChar();
        return ch == c;
    }
    
    public KeyEvent getKeyEvent() {
        return mKeyEvent;
    }

    public KeyboardEventInfo(KeyEvent ke) {
        mKeyEvent = ke;
    }
}
