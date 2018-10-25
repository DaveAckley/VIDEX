package com.putable.videx.std.vo.Slides;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.drivers.OIOLoadConfiguration;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.SlideDeck;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.riders.SOSIPoseRider;

@OIOTop
public class BasicSlideDeck extends EventAwareVO implements SlideDeck {

    private HTMLEditorKit mKit = new HTMLEditorKit();

    @OIO(value=OIO.FILENAME)
    private String mOIOPath = null;

    @OIO(value=OIO.BASE_DIRECTORY)
    private String mOIOBaseDir = null;
    
    @OIO(inline=false,extension="css")
    private String mCSS = "";
    private String mInstalledCSS = null;

    @OIO
    private int mSlideCount = 0;
    
    private void checkLoadCSS() {
        if (this.mCSS != null && this.mCSS != this.mInstalledCSS) {
            StyleSheet s = mKit.getStyleSheet();
            
            s.addRule(this.mCSS);
            this.mInstalledCSS = this.mCSS;
        }
    }
    
    public void setCSS(String css) {
        this.mCSS = css;
        checkLoadCSS();
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        checkLoadCSS();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // SlideDeck is currently invisible but that might change
    }

    @Override
    public boolean isFocusAware() {
        return true;
    }

    private boolean checkpointSave() {
        File dir = new File(this.mOIOBaseDir);
        File realbase = dir.getParentFile();
        World w = this.getWorld();
        if (w == null) throw new IllegalStateException();
        Configuration c = w.getConfiguration();
        if (!(c instanceof OIOLoadConfiguration))
            throw new IllegalStateException("DAVE IS A LOSER");
        OIOLoadConfiguration oc = (OIOLoadConfiguration) c; 
        OIOLoad loader = oc.getLoader();
        OIOAbleGlobalMap map = loader.getMap();
        OIOSave os = new OIOSave(realbase.toString());
        VO holdParent = this.getParent();
        this.clearParent();
        try {
            map.endLoading();
            os.save(this, map);
        } catch (IOException | OIOException e) {
            e.printStackTrace();
            System.err.println("SlideDeck save failed: " + e);
        }
        this.setParent(holdParent);
        return true;
    }
    @OIO
    private int mCurrentSlide = -1;

    @OIO
    private int mHiddenXStart = 50;

    @OIO
    private int mHiddenXIncrement = 100;

    @OIO
    private double mHiddenScale = 0.2;

    private boolean changeSlide(int incr) {
        return gotoSlide(mCurrentSlide + incr);
    }
    private int countSlides() {
        int count = 0;
        for (VO vo : this) {
            if (vo instanceof BasicSlide) ++count;
        }
        return count;
    }
    /**
     * Slide 0 is 'no slide, at beginning'
     * Slide 1..countSlides are the actual slide
     * Slide countSlides+1 is 'no slide, at end'
     * @param newslide
     * @return
     */
    private boolean gotoSlide(int newslide) {
        int slides = countSlides();
        if (newslide < 0) mCurrentSlide = 0; 
        else if (newslide > slides) mCurrentSlide = slides+1;
        else mCurrentSlide = newslide;
        setRiders();
        return true;
    }

    private void clearSOSIs(BasicSlide bs) {
        for (Rider d : bs.getRiders()) {
            if (d instanceof SOSIPoseRider) d.die();
        }
    }
    
    private void changeSOSI(BasicSlide bs, Pose dest) {
        clearSOSIs(bs);
        SOSIPoseRider sosi = new SOSIPoseRider(dest);
        bs.addPendingRider(sosi);
    }
    
    private void show(BasicSlide bs, int position) {
        Pose dest = new Pose();
        dest.setPAX(-0.005);
        dest.setPAY(400);
        dest.setS(7);
        changeSOSI(bs,dest);
    }
    private void hide(BasicSlide bs, int position) {
        Pose dest = new Pose();
        dest.setPAX(mHiddenXStart+position*mHiddenXIncrement);
        dest.setPAY(2000);
        dest.setS(mHiddenScale);
        changeSOSI(bs,dest);
    }
    private void setRiders() {
        int slideCount = 0;
        for (VO vo : this) {
            if (!(vo instanceof BasicSlide)) continue;
            BasicSlide bs = (BasicSlide) vo;
            ++slideCount;
            bs.deckStatus(this, mCurrentSlide, slideCount, mSlideCount);
        }
        mSlideCount = slideCount;
    }

    private void setRidersOLD() {
        int slideCount = 0;
        for (VO vo : this) {
            if (!(vo instanceof BasicSlide)) continue;
            BasicSlide bs = (BasicSlide) vo;
            if (++slideCount == mCurrentSlide) 
                show(bs,slideCount);
            else 
                hide(bs,slideCount);
        }
        mSlideCount = slideCount;
    }
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        if (kei.isKeyTyped(' ')) return changeSlide(1);
        KeyEvent ke = kei.getKeyEvent();
        if (ke.getID() == KeyEvent.KEY_RELEASED) {
            int code = ke.getKeyCode();
            switch (code) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                return changeSlide(1);
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                return changeSlide(-1);
            case KeyEvent.VK_HOME:
                return gotoSlide(0);
            case KeyEvent.VK_END:
                return gotoSlide(Integer.MAX_VALUE);
            default:
                break;
            }
        }
        
        if (ke.getID() != KeyEvent.KEY_TYPED) return false;
        //if (ke.getKeyChar() == '\022') return reload(); //^R
        if (ke.getKeyChar() == '\023') return checkpointSave(); //^S
        return false;
    }

}
