package com.putable.videx.std.vo;

import java.awt.event.KeyEvent;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

@OIOTop
public class ScreenBlankerVO extends EventAwareVO {
    @OIO
    private boolean mDisplayBelow = true;
    
    /**
     * Front-running keyboard overrides to catch 'b' to toggle blanking everything below this ScreenBlankerVO
     * 
     * @param kei
     * @return true if we took the event
     */
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        KeyEvent ke = kei.getKeyEvent();
        if (ke.getID() != KeyEvent.KEY_TYPED) return false;
        if (ke.getKeyChar() == 'Z') {
            mDisplayBelow = !mDisplayBelow;
            return true;  // handled
        }
        return false;
    }


    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        if (handleKeyboardEventHere(kei)) 
            return true;
        for (VO kid : this) {
            if (kid.handleKeyboardEvent(kei))
                return true;
        }
        return false;
    }

    @Override
    public void drawVO(VOGraphics2D v2d) {
        if (this.mDisplayBelow) super.drawVO(v2d);
    }


    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }


    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // Nothing to do
    }
}
