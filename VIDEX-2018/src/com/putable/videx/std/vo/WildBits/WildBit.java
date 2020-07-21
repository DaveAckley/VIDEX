package com.putable.videx.std.vo.WildBits;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Fonts;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;

public class WildBit extends EventAwareVO {
    public static final Font DEFAULT_BIT_FONT = Fonts.SINGLETON.getFont(
            "Inconsolata", Font.BOLD, 20);
    public static final Font DEFAULT_LABEL_FONT = Fonts.SINGLETON.getFont(
            "Gillius ADF", Font.ITALIC, 18);

    public static final int FONT_BOX_WIDTH = 22;
    public static final int FONT_BOX_HEIGHT = 22;

    private WildBitValue mValue;
    private String mLabel;
    private boolean mLabelPosted;
    private boolean mValueWritten;
    private Font mBitFont;
    private Font mLabelFont;

    public WildBit() {
        this(WildBitValue.ILLEGAL);
    }

    public WildBit(WildBitValue v) {
        this.setValue(v);
        this.setBitFont(DEFAULT_BIT_FONT);
        this.setLabelFont(DEFAULT_LABEL_FONT);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        // Nothing to do.
        return true;
    }
    
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        Color oldfg = g2d.getColor();
        Color oldbg = g2d.getBackground();
        Font oldFont = g2d.getFont();

        if (mValueWritten) {
            this.setBackground(this.mValue.backgroundColor);
            this.setForeground(this.mValue.foregroundColor);
        } else {
            this.setBackground(this.mValue.backgroundColorNoValueWritten);
            this.setForeground(this.mValue.foregroundColorNoValueWritten);
        }
        g2d.clearRect(0, 0, FONT_BOX_WIDTH, FONT_BOX_HEIGHT);
        if (mValueWritten) {
            g2d.setFont(mBitFont);
            FontMetrics fm = getUnscrewedFontMetrics(g2d, 0);
            int x = (FONT_BOX_WIDTH - fm.stringWidth(mValue.code)) / 2;
            int y = fm.getAscent()
                    + (FONT_BOX_HEIGHT - (fm.getAscent() + fm.getDescent()))
                    / 2;
            g2d.drawString(mValue.code, x, y);

        }
        if (mLabelPosted && mLabel != null) {
            AffineTransform at = g2d.getTransform();
            double ninety = 90 * Math.PI / 180;
            g2d.rotate(ninety);
            g2d.setFont(mLabelFont);

            FontMetrics fm = getUnscrewedFontMetrics(g2d, ninety);
            int x = FONT_BOX_HEIGHT + FONT_BOX_HEIGHT / 3;
            int y = -FONT_BOX_WIDTH + fm.getAscent()
                    + (FONT_BOX_WIDTH - (fm.getAscent() + fm.getDescent())) / 2;

            g2d.drawString(mLabel, x, y);
            g2d.setTransform(at);
        }
        g2d.setFont(oldFont);
    }

    public WildBitValue getValue() {
        return mValue;
    }

    public void setValue(WildBitValue mValue) {
        if (mValue == null)
            throw new NullPointerException();
        if (mValue != this.mValue) {
            this.mValue = mValue;
        }
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        if (mLabel == null)
            mLabel = "";
        this.mLabel = mLabel;
    }

    public boolean isLabelPosted() {
        return mLabelPosted;
    }

    public void setLabelPosted(boolean mLabelPosted) {
        this.mLabelPosted = mLabelPosted;
    }

    public boolean isValueWritten() {
        return mValueWritten;
    }

    public void setValueWritten(boolean mValueWritten) {
        this.mValueWritten = mValueWritten;
    }

    public Font getBitFont() {
        return mBitFont;
    }

    public void setBitFont(Font mBitFont) {
        if (mBitFont == null)
            throw new NullPointerException();
        this.mBitFont = mBitFont;
    }

    public Font getLabelFont() {
        return mLabelFont;
    }

    public void setLabelFont(Font mLabelFont) {
        if (mLabelFont == null)
            throw new NullPointerException();
        this.mLabelFont = mLabelFont;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

}
