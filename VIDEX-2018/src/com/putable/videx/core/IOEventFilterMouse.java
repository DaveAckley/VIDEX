package com.putable.videx.core;

import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.interfaces.IOEventFilter;

public interface IOEventFilterMouse extends IOEventFilter {

    boolean matches(MouseEventInfo mei);
}
