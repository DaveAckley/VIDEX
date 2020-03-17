package com.putable.videx.std.vo.image;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.nio.file.Path;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.HittableImage;
import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.std.vo.ReadOnlyDirectoryManager;
import com.putable.videx.utils.FileUtils;

public abstract class ImageViewer extends EventAwareVO {
    public abstract ReadOnlyDirectoryManager getPersistentStateManager() ;
    
    private boolean mInitted = false;
    private int mPathIndex; // The RODM index of mImages[1]
    private int mPathCount;
    private HittableImage[] mImages = { null, null, null }; // prev curr next    
    public HittableImage tryLoadImageIndex(int index) {
        Path path = getPersistentStateManager().getPath(index);
        return tryLoadImage(path);
    }
    public HittableImage tryLoadImage(Path path) {
        if (path == null) return null;
        HittableImage ret = null;
        byte[] data = FileUtils.readWholeFileAsByteArray(path);
        if (data == null) return ret;
        ret = new HittableImage(this);
        ret.setImageFromBytesIfPossible(path.toString(), data);
        ret.setAlphaHittable(false);
        ret.setImagePath(path);
        return ret;
    }

    private void configureFromHittableImage(HittableImage hi) {
        if (hi != null) {
            Path name = hi.getImagePath().getFileName();
            System.out.println("CONHITIM FOR "+name);
            ReadOnlyDirectoryManager rodm = this.getPersistentStateManager();
            if (rodm != null) {
                Pose p = null;
                String layout = rodm.getPersistentDataFor(name);
                if (layout != null) {
                    System.out.println("CONHITIM LAYOUT "+layout);
                    p = Pose.destringify(layout);
                }
                if (p == null)
                    p = hi.makeDefaultPose();
                this.getPose().copy(p);
            }
        }
    }

    public HittableImage getCurrentImage() {
        initIfNeeded();
        if (mImages[1] == null) {
            if (this.goForward() || this.goBackward()) { // Hope for something!
                /* EMPTY */
            } 
        }
        return mImages[1];
    }

    private void persistCurrentImageVO() {
        HittableImage hi = getCurrentImage();
        if (hi == null) return;
        ReadOnlyDirectoryManager rodm = getPersistentStateManager();
        if (rodm == null) return;
        Path p = hi.getImagePath().getFileName();
        String layout = this.getPose().stringify();
        rodm.updatePersistentDataFor(p, layout);
    }

    private void initIfNeeded() {
        if (mInitted) return;
        mPathCount = getPersistentStateManager().getPathCount();

        for (int i = 0; i < 3; ++i)  mImages[i] = tryLoadImageIndex(i-1);
        
        mPathIndex = 0;
        mInitted = true;

        if (mImages[1] != null)
            resetCurrentImageVOToPersisted();
    }
    
    private void printImages(String msg) {
        System.out.print(msg+" (@"+mPathIndex+") ");
        for (int i = 0; i < mImages.length; ++i) {
            System.out.print(i+"="+mImages[i]+" ");
        }
        System.out.println();
    }
    public boolean goForward() {
        initIfNeeded();
        printImages("goF PRE");

        // itr invariant is previous() returns onscreen ([1]) and next() returns ondeck([2])

        if (mImages[2] == null) return false; // no place to advance to
        mImages[0] = mImages[1];
        mImages[1] = mImages[2];
        ++mPathIndex;
        mImages[2] = tryLoadImageIndex(mPathIndex+1);
        this.configureFromHittableImage(mImages[1]);
        printImages("goF POST");
        return true;
    }

    public boolean goBackward() {
        initIfNeeded();
        printImages("gob PRE");

        if (mImages[0] == null) return false; // no place to retreat to
        mImages[2] = mImages[1];  // Ondeck is old onscreen
        mImages[1] = mImages[0];  // Onscreen is old previous
        --mPathIndex;
        mImages[0] = tryLoadImageIndex(mPathIndex-1);
        this.configureFromHittableImage(mImages[1]);
        printImages("gob POST");
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        //System.out.println("DTVO "+this);
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
                if (ch == 'p') {
                    System.err.println("YOINK");
                    this.persistCurrentImageVO();
                    return true;
                }
                if (ch == 'r') {
                    System.err.println("RESET");
                    this.resetCurrentImageVOToPersisted();
                }
            }
            return false;
        }
        return false;
    }
    private void resetCurrentImageVOToPersisted() {
        this.configureFromHittableImage(mImages[1]);
    }


}
