package com.putable.videx.std.vo.image;

import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ListIterator;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.std.vo.ReadOnlyDirectoryManager.RODMIterator;

public class TimeLinePlayer extends ImageViewer {

    @OIO(owned=false)
    private PNGImageDirectoryManager mImageDirectory = null; 
    private RODMIterator mIterator = null;  

    private ListIterator<Path> getIteratorIfPossible() {
        if (mImageDirectory == null) return null;
        if (mIterator == null || !mIterator.isValid())
            mIterator = mImageDirectory.getIterator();
        return mIterator;
    }
    public void setImageDirectory(PNGImageDirectoryManager idvo) {
        mImageDirectory = idvo;
        mIterator = null;
    }

    @OIO
    private int mUpdatesThisFrame = 0;
    
    @OIO
    private int mUpdatesPerFrame = 2;  // + -> forward, - -> backward
    
    private int mUpdateStartTimer = 0;
    private final static int UPDATE_START_COUNT = 50;
    private final static int MAX_UPDATES_PER_FRAME = 30; // slowest advance

    @Override
    public boolean updateThisVO(Stage stage) {
        if (mUpdatesPerFrame == 0) {
            // We're stopped.  Just track startup timer.
            if (mUpdateStartTimer > 0) --mUpdateStartTimer;
        } else if (mUpdatesPerFrame > 0) {
            if (++mUpdatesThisFrame >= mUpdatesPerFrame) {
                this.goForward();
                mUpdatesThisFrame = 0;
            }
        } else /* mUpdatesPerFrame < 0 */ {
            if (++mUpdatesThisFrame >= -mUpdatesPerFrame) {
                this.goBackward();
                mUpdatesThisFrame = 0;
            }
        }
        return super.updateThisVO(stage);
    }

    @Override
    public ListIterator<Path> pathPosition() {
        return this.getIteratorIfPossible();
    }

    private void checkMoveStart(boolean fwd) {
        if (this.mUpdatesPerFrame == 0) {
            if (this.mUpdateStartTimer == 0) {
                if (fwd) this.goForward(); else this.goBackward();
                mUpdatesThisFrame = 0;
                this.mUpdateStartTimer = UPDATE_START_COUNT; 
            } else if (fwd) {
                this.mUpdatesPerFrame = MAX_UPDATES_PER_FRAME;
                this.mUpdateStartTimer = 0;
            } else /* !fwd */ {
                this.mUpdatesPerFrame = -MAX_UPDATES_PER_FRAME;
                this.mUpdateStartTimer = 0;
            }
        } else {
            if (mUpdatesPerFrame > 1 && fwd) {
                --mUpdatesPerFrame; // accelerate additive fwd
            } else if (mUpdatesPerFrame > 0 && !fwd) {
                mUpdatesPerFrame *= 2; // decelerate mult
                if (mUpdatesPerFrame >= UPDATE_START_COUNT) {
                    mUpdatesPerFrame = 0;
                }
            } else if (mUpdatesPerFrame < -1 && !fwd) {
                ++mUpdatesPerFrame; // accel add bkwd
            } else if (mUpdatesPerFrame < 0 && fwd) {
                mUpdatesPerFrame *= 2; // decelerate mult
                if (-mUpdatesPerFrame >= UPDATE_START_COUNT) {
                    mUpdatesPerFrame = 0;
                }
            }
        }
    }
    
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        if (isMouseTarget()) {
            KeyEvent ke = kei.getKeyEvent();
            if (ke.getID() == KeyEvent.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (ch == ' ' || ch == 'f') {
                    checkMoveStart(true);
                    return true;
                }
                if (ch == '\377' || ch == 'b') {
                    checkMoveStart(false);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

}
