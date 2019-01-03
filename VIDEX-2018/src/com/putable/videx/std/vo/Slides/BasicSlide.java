package com.putable.videx.std.vo.Slides;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Fonts;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.Slide;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.std.riders.TogglePresentationRider;
import com.putable.videx.std.riders.ZoomOutRider;
import com.putable.videx.std.specialevents.RunGenericSpecialEventInfo;
import com.putable.videx.std.vo.EditableTextLine;
import com.putable.videx.std.vo.PopupTextLineEntry;
import com.putable.videx.std.vo.TimedNotification;
import com.putable.videx.std.vo.image.OIOImage;
import com.putable.videx.utils.FileUtils;

@OIOTop
public class BasicSlide extends EventAwareVO implements Slide {

    {
        this.setIsFocusAware(true);
    }
    
    @OIO
    private Point2D mSlideSize = new Point2D.Double(500, 400);

    @OIO
    private String mSlideName = null;

    @OIO
    private boolean mDrawBackground = true;

    @OIO(inline = false, extension = ".html")
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

    @OIO(obsolete=true)
    private LinkedList<Integer> mListOfNumbersDeleteMeXXX = new LinkedList<Integer>();

    @OIO
    private SlideRiderGenerator mRiderGenerator = new ThreeStopRiderGenerator();

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
        if (this.findFirstRider(BasicSlideRider.class) != null) return;
        BasicSlideRider bsr = (BasicSlideRider) this.mRiderGenerator.generate();
        this.addPendingRider(bsr);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        if (this.mTextString != null
                && !this.mTextString.equals(this.mLoadedText)) {
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
            g2d.clearRect(0, 0, (int) mSlideSize.getX(),
                    (int) mSlideSize.getY());

        if (v2d.isRenderingToHitmap())
            return;

        if (mDrawBackground) {
            Color fg = g2d.getColor();
            g2d.setColor(mBorderColor);
            g2d.drawRect(0, 0, (int) mSlideSize.getX(),
                    (int) mSlideSize.getY());
            g2d.setColor(fg);
        }
        Font oldfont = g2d.getFont();
        g2d.setFont(mFont);
        g2d.translate(mOrigin.getX(), mOrigin.getY());
        mText.setSize((int) (mHTMLWidthFraction * mSlideSize.getX()),
                (int) mSlideSize.getY());
        mText.setVerticalAlignment(SwingConstants.TOP);
        mText.setForeground(this.getForeground());
        mText.setBackground(this.getBackground());
        // setFont on mText vvvvvvv kills drawing speed! And doesn't seem to
        // matter...
        // mText.setFont(this.mFont);
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
        if (isMouseTarget()) {
            KeyEvent ke = kei.getKeyEvent();
            if (ke.getID() == KeyEvent.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (ch == 'I' || ch == 'Z') {
                    this.addPendingChild(new PopupTextLineEntry(this,
                            "Image file:", this.mLastImagePath,""+ch));
                    return true;
                }
                if (ch == '#') {
                    TimedNotification.postOn(this, "Slide: " + FileUtils.toLex(this.getOnum()));
                    return true;
                }
                if (ch == 'R') {
                    RunGenericSpecialEventInfo runcmd = new RunGenericSpecialEventInfo();
                    this.handleSpecialEvent(runcmd);
                    return true;
                }
                if (ch == 'E') {
                    for (VO vo : this) {
                        vo.setEnabled(true);
                    }
                    TimedNotification.postOn(this, "Kids enabled");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deckStatus(SlideDeck sd, int currentNum, int yourNum,
            int totalSlides) {
        for (Rider r : this.getRiders()) {
            if (r instanceof BasicSlideRider) {
                BasicSlideRider bsr = (BasicSlideRider) r;
                bsr.deckStatus(sd, currentNum, yourNum, totalSlides);
            }
        }
    }

    @OIO
    private String mLastImagePath = "/data/ackley/AV/MFM/T2sdayUpdate/episodes/";

    private void tryAddImage(String imgpath,String stringload) {
        OIOImage img = OIOImage.makeFromPath(imgpath);
        if (img == null) 
            System.out.println("Couldn't make image from "+imgpath);
        else {
            Rider r;
            if (stringload.equals("I")) r = new TogglePresentationRider();
            else if (stringload.equals("Z"))
                r = new ZoomOutRider();
            else throw new IllegalArgumentException("What's a '"+stringload+"'?");
            img.addPendingRider(r);
            mLastImagePath = imgpath;
            this.addPendingChild(img);
        }
    }

    @Override
    public boolean handleSpecialEventHere(SpecialEventInfo mei) {
        if (mei instanceof EditableTextLine.TextLineEnteredEventInfo) {
            EditableTextLine.TextLineEnteredEventInfo info = (EditableTextLine.TextLineEnteredEventInfo) mei;
            String imgpath = (String) info.getValue();
            tryAddImage(imgpath,info.getStringLoad());
            return true;
        }
        return false;
    }

}
