package com.putable.videx.std.vo.Slides;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Fonts;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.Slide;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.interfaces.Stage;

@OIOTop
public class BasicSlide extends EventAwareVO implements Slide {
    @OIO
    private Point2D mSlideSize = new Point2D.Double(640, 360);

    @OIO
    private String mSlideName = null;

    @OIO
    private boolean mDrawBackground = true;

    @OIO(inline=false,extension=".html")
    private String mTextString = null;

    @OIO
    private double mHTMLWidthFraction = 0.60;
    private String mLoadedText = null;
    private JLabel mText = new JLabel();

    @OIO
    private Point2D mOrigin = new Point2D.Double(5, 5);

    @OIO
    private int mFontSize = 18;
    private Font mFont = Fonts.SINGLETON.getFont("Gillius ADF", Font.PLAIN,
            mFontSize);

    @OIO
    private Color mBorderColor = Color.black;

    @OIO
    private LinkedList<Integer> mListOfNumbersDeleteMeXXX =
        new LinkedList<Integer>();

    @OIO
    private SlideRiderGenerator mRiderGenerator = new BasicSlideRiderGenerator();
    
    @Override
    public String getSlideName() {
        if (mSlideName == null) 
            throw new IllegalStateException();
        return mSlideName;
    }
    
    public void setSlideName(String name) {
        mSlideName = name;
    }
    public void setText(String t) {
        mTextString = t;
    }

    public String getText() {
        return mTextString;
    }
    
    public BasicSlide() {
        this.setBackground(Color.black);
        this.setForeground(Color.yellow);
        this.mSlideName = "unknown";
    }
    
    public BasicSlide(String name) {
        this();
        this.mSlideName = name;
    }

    private void checkRider() {
        for (Rider r : this.getRiders()) {
            if (r instanceof BasicSlideRider) return;
        }
        BasicSlideRider bsr = (BasicSlideRider) this.mRiderGenerator.generate();
        this.addRider(bsr);
    }
    
    @Override
    public boolean updateThisVO(Stage stage) {
        if (this.mTextString != null && !this.mTextString.equals(this.mLoadedText)) {
            mLoadedText = mTextString;
            mText.setText(mLoadedText);
        }
        checkRider();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        if (mDrawBackground)
            g2d.clearRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
        
        if (v2d.isRenderingToHitmap()) return;

        if (mDrawBackground) {
            Color fg = g2d.getColor();
            g2d.setColor(mBorderColor);
            g2d.drawRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
            g2d.setColor(fg);
        }
        Font oldfont = g2d.getFont();
        g2d.setFont(mFont);
        g2d.translate(mOrigin.getX(), mOrigin.getY());
        mText.setSize((int) (mHTMLWidthFraction*mSlideSize.getX()), 
                (int) mSlideSize.getY());
        mText.setVerticalAlignment(SwingConstants.TOP);
        mText.setForeground(this.getForeground());
        mText.setBackground(this.getBackground());
// setFont on mText vvvvvvv kills drawing speed!  And doesn't seem to matter...
//        mText.setFont(this.mFont);
        mText.paint(g2d);
        g2d.translate(-mOrigin.getX(), -mOrigin.getY());
        g2d.setFont(oldfont);
    }

    @Override
    public String theHTML(String html) {
        if (html != null)
            this.setText(html);
        return this.getText();
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

    @Override
    public void deckStatus(SlideDeck sd, int currentNum, int yourNum, int totalSlides) {
        for (Rider r : this.getRiders()) {
            if (r instanceof BasicSlideRider) {
                BasicSlideRider bsr = (BasicSlideRider) r;
                bsr.deckStatus(sd, currentNum, yourNum, totalSlides);
            }
        }
    }
}
