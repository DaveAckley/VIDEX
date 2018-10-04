package com.putable.videx.std.vo.WildBits;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.std.vo.ConnectorLine;

public abstract class WildBitVectorOp extends EventAwareVO {

    public abstract RunningWildBitOp getOp();

    private class LinkedVec {
        WildBitVector mOperand;
        ConnectorLine mLink;

        LinkedVec(WildBitVector wbv, ConnectorLine cl) {
            mOperand = wbv;
            mLink = cl;
        }
    }

    private List<LinkedVec> mOutputs = new LinkedList<LinkedVec>();

    private List<LinkedVec> mInputs = new LinkedList<LinkedVec>();

    private VO getRoot() {
        VO vo = this;
        while (vo.getParent() != null && vo.getParent().getParent() != null)
            vo = vo.getParent();
        return vo;
    }
    private void add(List<LinkedVec> llv, WildBitVector wbv, boolean headto,
            boolean headfrom) {
        for (LinkedVec lv : llv)
            if (lv.mOperand == wbv)
                return;
        ConnectorLine cl = new ConnectorLine(wbv, WildBitVectorOp.this, headto,
                headfrom);
        cl.setLineThickness(1);
        WildBitVectorOp.this.addPendingChild(cl);
        llv.add(new LinkedVec(wbv, cl));
    }

    public void addInput(WildBitVector wbv) {
        add(mInputs, wbv, true, false);
    }

    public void addOutput(WildBitVector wbv) {
        add(mOutputs, wbv, false, true);
    }

    private void remove(List<LinkedVec> llv, WildBitVector wbv) {
        for (Iterator<LinkedVec> itr = llv.iterator(); itr.hasNext();) {
            LinkedVec lv = itr.next();
            if (wbv == lv.mOperand) {
                lv.mLink.killVO();
                itr.remove();
            }
            // Keep scanning in case dupes possible?
        }

    }

    public void removeInput(WildBitVector wbv) {
        remove(mInputs, wbv);
    }

    public void removeOutput(WildBitVector wbv) {
        remove(mOutputs, wbv);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        int length = -1;
        for (LinkedVec lv : mInputs) {
            WildBitVector wbv = lv.mOperand;
            if (wbv.getLength() > length)
                length = wbv.getLength();
        }
        if (length < 1)
            return true;
        for (int i = 0; i < length; ++i) {
            WildBitValue val = null;
            for (LinkedVec lv : mInputs) {
                WildBitVector wbv = lv.mOperand;
                if (i >= wbv.getLength())
                    continue;
                WildBitValue v = wbv.getAt(i).getValue();
                if (val == null)
                    val = this.getOp().performUnaryOp(v);
                else
                    val = this.getOp().performRunningOp(val, v);
            }
            for (LinkedVec lv : mOutputs) {
                WildBitVector wbv = lv.mOperand;
                if (i >= wbv.getLength())
                    continue;
                wbv.getAt(i).setValue(val);
            }
        }
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        String op = getOp().getOpName();
        Graphics2D g2d = v2d.getGraphics2D();
        FontMetrics fm = this.getUnscrewedFontMetrics(g2d, 0);
        Rectangle2D bounds = fm.getStringBounds(op, g2d);
        final double EXPANDER = 0.15;
        double extraWidth = EXPANDER * bounds.getWidth();
        double extraHeight = EXPANDER * bounds.getHeight();
        double x = bounds.getMinX() - extraWidth / 2;
        double y = bounds.getMinY() - extraHeight / 2;
        double w = bounds.getWidth() + extraWidth;
        double h = bounds.getHeight() + extraHeight;
        Ellipse2D e = new Ellipse2D.Double();
        e.setFrameFromDiagonal(x, y, x + w, y + h);
        Color fg = g2d.getColor();
        Color bg = g2d.getBackground();
        g2d.setColor(bg);
        g2d.fill(e);
        g2d.setColor(fg);
        // g2d.draw(e);
        g2d.drawString(op, 0, 0);
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) { 
        return false;
    }
}
