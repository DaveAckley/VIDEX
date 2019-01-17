package com.putable.videx.std.vo.image;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ListIterator;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.HittableImage;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.utils.FileUtils;

public abstract class ImageViewer extends EventAwareVO {
    public abstract ListIterator<Path> pathPosition() ;
    
    private HittableImage[] mImages = { null, null, null }; // prev curr next    
    
    public HittableImage tryLoadImage(Path path) {
        HittableImage ret = null;
        byte[] data = FileUtils.readWholeFileAsByteArray(path);
        if (data == null) return ret;
        ret = new HittableImage(this);
        ret.setImageFromBytesIfPossible(path.toString(), data);
        ret.setAlphaHittable(false);
        ret.setImagePath(path);
        return ret;
    }

    public HittableImage getCurrentImage() {
        if (mImages[1] == null) {
            if (this.goForward() || this.goBackward()) { // Hope for something!
                /* EMPTY */
            } 
        }
        return mImages[1];
    }
    
    public boolean goForward() {
        ListIterator<Path> itr = pathPosition();
        if (!itr.hasNext()) return false;
        Path p = itr.next();
        mImages[0] = mImages[1];
        mImages[1] = mImages[2];
        mImages[2] = tryLoadImage(p);
        return true;
    }

    public boolean goBackward() {
        ListIterator<Path> itr = pathPosition();
        if (!itr.hasPrevious()) return false;
        Path p = itr.previous();
        mImages[2] = mImages[1];
        mImages[1] = mImages[0];
        mImages[0] = tryLoadImage(p);
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        // System.out.println("DTVO "+this);
        HittableImage img = this.getCurrentImage();
        // Grab some territory image or no?
        g2d.fillRect(0, 0, 1000,1000);
        if (img == null) {
            g2d.drawString("NO CURRENT IMAGE", 100, 100);
        } else 
            img.drawImage(v2d, 0, 0);
    }

    private boolean checkLoadImage(int index) {
        //System.err.println("checkLoadImage "+index+" i do squat");
        return true;
    }
    
    @Override
    public boolean updateThisVO(Stage stage) {
        for (int i = 0; i < 3; ++i)
            if (checkLoadImage(i)) return true; // just do one per update
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        if (isMouseTarget()) {
            KeyEvent ke = kei.getKeyEvent();
            if (ke.getID() == KeyEvent.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (ch == ' ' || ch == 'f') {
                    this.goForward();
                    return true;
                }
                if (ch == '\377' || ch == 'b') {
                    this.goBackward();
                    return true;
                }
            }
            return false;
        }
        return false;
    }

}
