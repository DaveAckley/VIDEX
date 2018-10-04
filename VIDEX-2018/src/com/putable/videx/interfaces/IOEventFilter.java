package com.putable.videx.interfaces;

import com.putable.videx.core.events.IOEvent;

public interface IOEventFilter {
    boolean matches(IOEvent ioe);
}
