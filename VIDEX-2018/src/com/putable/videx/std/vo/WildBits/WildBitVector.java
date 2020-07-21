package com.putable.videx.std.vo.WildBits;


import java.awt.Color;
import java.awt.event.KeyEvent;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.SXRandom;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Stage;

@OIOTop
public class WildBitVector extends EventAwareVO {
    private SXRandom mRandom;
    void drawValues(boolean doit) {
        for (int i = 0; i < mBits.length; ++i) {
            mBits[i].setValueWritten(doit);
        }
    }
    void randomizeBits(int offPct, int onPct) { // rest is wildpct
        if (mRandom != null) {
            for (int i = 0; i < mBits.length; ++i) {
                int pct = mRandom.between(0, 100);
                WildBitValue val = WildBitValue.WILD;
                if (pct < offPct) val = WildBitValue.ZERO;
                else if (pct < offPct + onPct) val = WildBitValue.ONE;
                mBits[i].setValue(val);
            }
        }
    }
    /*
    public void drawThisVO(VOGraphics2D v2d) {
        super.drawVO(v2d);
        Graphics2D g2d = v2d.getGraphics2D();
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < mBits.length; ++i) {
            mBits[i].drawVO(v2d);
        }
        g2d.setColor(Color.YELLOW);
        g2d.fill(new Rectangle2D.Double(0,0,1000,1000));
    }
*/
 
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

    public WildBitVector() {
        this(20);
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

    private void addBitsIfNeeded() {
        if (mBits[0].getParent() != this) {
            for (int i = 0; i < mBits.length; ++i) {
                this.addPendingChild(mBits[i]);
            }
        }
                
    }
    @Override
    public boolean updateThisVO(Stage stage) {
        if (mRandom == null) mRandom = stage.getRandom();
        addBitsIfNeeded();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        int width = mBits.length*WildBit.FONT_BOX_WIDTH;
        v2d.getGraphics2D().setColor(Color.YELLOW);
        v2d.getGraphics2D().setBackground(Color.YELLOW);
        v2d.getGraphics2D().clearRect(0, 0, width, WildBit.FONT_BOX_HEIGHT);
    }
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        System.out.println("WILDBITHKEYB "+kei);
        if (true/*isMouseTarget()*/) {
            System.out.println("XONG WILDBITHKEYB "+kei);
            KeyEvent ke = kei.getKeyEvent();
            System.out.println("XONG "+ke+ " KCH "+ke.getKeyChar());
            if (ke.getID() == KeyEvent.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (ch == 'R') {
                    randomizeBits(33,33);
                    return true;
                }
                if (ch == 'V' || ch == 'v') {
                    drawValues(ch== 'V');
                    return true;
                }
            }
        }
        return false;
    }

}
