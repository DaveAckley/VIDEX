package com.putable.videx.core.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseEventInfo extends IOEvent {
    private final MouseEvent mMouseEvent;
    private final MouseWheelEvent mMouseWheelEvent;

    @Override
    public String toString() {
        if (mMouseEvent != null)
            return "MouseEventInfo[ME=" + mMouseEvent + "]";

        if (mMouseWheelEvent != null)
            return "MouseEventInfo[MWE=" + mMouseWheelEvent + "]";

        return "MouseEventInfo[]";
    }

    public MouseEventInfo(MouseEvent me) {
        mMouseEvent = me;
        mMouseWheelEvent = null;
    }

    public MouseEventInfo(MouseWheelEvent mwe) {
        mMouseEvent = null;
        mMouseWheelEvent = mwe;
    }

    public MouseEvent getMouseEvent() {
        return mMouseEvent;
    }

    public MouseWheelEvent getMouseWheelEvent() {
        return mMouseWheelEvent;
    }

}
