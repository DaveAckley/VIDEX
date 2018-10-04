package com.putable.videx.std.vo.WildBits;


import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;

public class WildBitVector extends EventAwareVO {

    private WildBit[] mBits; // Second ref to selected children

    public WildBit getAt(int index) {
        return mBits[index];
    }
    public void reconfigureBits() {
        int width = WildBit.FONT_BOX_WIDTH;
        for (int i = 0; i < mBits.length; ++i) {
            StandardVO wb = mBits[i];
            wb.getPose().setPAX(i * width);
        }
    }

    public int getLength() { return mBits.length; }
    
    public void setBits(int fromInt) {
        setWildBits(fromInt, 0xffffffff);
    }

    public void setWildBits(int fromInt, int mask) {
        int count = mBits.length;
        if (count > 32) count = 32;
        for (int i = 0; i < count; ++i) {
            if ((mask&1)==0) 
                mBits[i].setValue(WildBitValue.WILD);
            else 
                mBits[i].setValue((fromInt&1)==0?WildBitValue.ZERO:WildBitValue.ONE);
            mask >>>= 1;
            fromInt >>>= 1;
        }
    }

    public WildBitVector(int length) {
        if (length <= 0)
            throw new IllegalStateException();
        mBits = new WildBit[length];
        for (int i = 0; i < length; ++i) {
            WildBit wb = new WildBit();
            wb.setValueWritten(true);
            wb.setIsMouseAware(false);
            this.addPendingChild(wb);
            mBits[i] = wb;
        }
        reconfigureBits();
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        // Nothing to do as yet
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        int width = mBits.length*WildBit.FONT_BOX_WIDTH;
        v2d.getGraphics2D().clearRect(0, 0, width, WildBit.FONT_BOX_HEIGHT);
    }
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

}
