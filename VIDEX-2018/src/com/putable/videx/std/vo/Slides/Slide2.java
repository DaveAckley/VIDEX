package com.putable.videx.std.vo.Slides;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JLabel;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Fonts;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;

public class Slide2 extends EventAwareVO {
    private Point2D mSlideSize = new Point2D.Double(400,300);
    private JLabel mText = new JLabel();
    private Point2D mOrigin = new Point2D.Double(5,5);
    private int mFontSize = 18;
    private Font mFont = Fonts.SINGLETON.getFont("Gillius ADF", Font.PLAIN, mFontSize);

    public void setText(File file) {
        String text;
        try {
            byte[] encoded = Files.readAllBytes(file.toPath());
            text = new String(encoded);
        }
        catch (IOException e) {
           text = "IOException: "+ e;
        }
        setText("<HTML>"+text);
    }
    
    public void setText(String t) {
        mText.setText(t);
    }
    
    public Slide2() {
        this.setBackground(Color.black);
        this.setForeground(Color.yellow);
        this.getPose().setS(5);
    }
    @Override
    public boolean updateThisVO(Stage stage) {
        // Nothing to do so far
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        g2d.clearRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
        g2d.drawRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
        Font oldfont = g2d.getFont();
        g2d.setFont(mFont);
        g2d.translate(mOrigin.getX(), mOrigin.getY());
        //the fontMetrics stringWidth and height can be replaced by
        //getLabel().getPreferredSize() if needed
        mText.setSize(mText.getPreferredSize());
        //mText.setSize((int) mSlideSize.getX(), (int) mSlideSize.getY());
        mText.setForeground(this.getForeground());
        mText.setBackground(this.getBackground());
        mText.setFont(this.mFont);
        mText.paint(g2d);
        //g2d.drawString("POo", 0, 0);
        g2d.translate(-mOrigin.getX(), -mOrigin.getY());
        g2d.setFont(oldfont);
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

}
