package com.putable.videx.std.vo.image;

import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ListIterator;
import java.util.TreeMap;

import com.putable.videx.core.HittableImage;
import com.putable.videx.core.Pose;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.std.vo.TimedNotification;
import com.putable.videx.std.vo.ReadOnlyDirectoryManager;
import com.putable.videx.std.vo.ReadOnlyDirectoryManager.RODMIterator;

public class TimeLinePlayer extends ImageViewer {

    @OIO(owned = false)
    private PNGImageDirectoryManager mImageDirectory = null;
    private RODMIterator mIterator = null;

    @Override
    public ReadOnlyDirectoryManager getPersistentStateManager() {
        return mImageDirectory;
    }

    @OIO
    private boolean mShowAll = true;

    @OIO
    private TreeMap<String, String> mImageInfoMap = new TreeMap<String, String>();

    // XXX TIMELINEINFO IS CURRENTLY UNFINISHED AND UNUSED! GOING WITH LOW-TECH
    // XXX DIRECTORY-BASED FILTERING INSTEAD
    public static class TimeLineInfo {
        private boolean mIsShown = false; // by default, if there's an entry,
                                          // it's a suppression
        private Pose mDisplayPose = new Pose(); // Position to display
        private int mDwellTime = 3; // updates to display

        public String toString() {
            return mIsShown + " " + mDwellTime + " " + mDisplayPose;
        }

        public static TimeLineInfo fromString(String s) {
            return null;
        }
    }

    // XXX TIMELINEITERATOR ALSO CURRENTLY UNFINISHED AND UNUSED!
    public class TimeLineIterator implements ListIterator<Path> {
        private ListIterator<Path> mRawItr;
        private Path mFilteredNext = null;
        private Path mFilteredPrev = null;

        TimeLineIterator(ListIterator<Path> rawitr) {
            mRawItr = rawitr;
        }

        private TimeLineInfo getInfoIfAny(Path p) {
            String pathName = p.toString();
            String info = mImageInfoMap.get(pathName);
            if (info == null)
                return null;
            TimeLineInfo tli = TimeLineInfo.fromString(info);
            if (tli == null) {
                System.out.println("TLI PARSE FAILURE ON '" + info + "'");
                tli = new TimeLineInfo(); // Default to suppress
            }
            return tli;

        }

        private Path findNextIfAny() {
            while (mFilteredNext == null && mRawItr.hasNext()) {
                Path maybe = mRawItr.next();
                TimeLineInfo tli = getInfoIfAny(maybe);
                /* XXX UNFINISHED if (tli == null || isAllowed(maybe))
                    mFilteredNext = maybe; */
                throw new UnsupportedOperationException("UNWRITTEN CODE");
            }
            return mFilteredNext;
        }

        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Path next() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean hasPrevious() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Path previous() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int nextIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int previousIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void remove() {
            // TODO Auto-generated method stub

        }

        @Override
        public void set(Path e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void add(Path e) {
            // TODO Auto-generated method stub

        }
    }

    private ListIterator<Path> getIteratorIfPossible() {
        if (mImageDirectory == null)
            return null;
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
    private int mUpdatesPerFrame = 2; // + -> forward, - -> backward

    private int mUpdateStartTimer = 0;
    private final static int UPDATE_START_COUNT = 50;
    private final static int MIN_UPDATES_PER_FRAME = 1; // fastest advance
    private final static int MAX_UPDATES_PER_FRAME = 20; // slowest advance

    private boolean imageSaysStop(HittableImage hi) {
        if (hi == null) return false;
        Path p = hi.getImagePath();
        if (p == null) return false;
        String n = p.toString();
        if (n.contains("-p")) {
            TimedNotification.postOn(this.getParent(), "Pause");
            return true;
        }
        return false;
    }
    @Override
    public boolean updateThisVO(Stage stage) {
        if (mUpdatesPerFrame == 0) {
            // We're stopped. Just track startup timer.
            if (mUpdateStartTimer > 0)
                --mUpdateStartTimer;
        } else if (mUpdatesPerFrame > 0) {
            if (++mUpdatesThisFrame >= mUpdatesPerFrame) {
                this.goForward();
                HittableImage on = this.getCurrentImage();
                if (imageSaysStop(on)) mUpdatesPerFrame = 0;
                mUpdatesThisFrame = 0;
            }
        } else /* mUpdatesPerFrame < 0 */ {
            if (++mUpdatesThisFrame >= -mUpdatesPerFrame) {
                this.goBackward();
                HittableImage on = this.getCurrentImage();
                if (imageSaysStop(on)) mUpdatesPerFrame = 0;
                mUpdatesThisFrame = 0;
            }
        }
        return super.updateThisVO(stage);
    }

    private void checkMoveStart(boolean fwd,boolean maxspd) {
        if (fwd && !maxspd) {
            this.mUpdatesPerFrame = 0;
            this.mUpdateStartTimer = 0;
            this.goForward();
            return;
        }
        if (fwd && maxspd) {
            this.mUpdatesPerFrame = MIN_UPDATES_PER_FRAME;
            this.goForward();
            this.mUpdateStartTimer = 0;
            return;
        }
        if (!fwd && !maxspd) {
            this.mUpdatesPerFrame = 0;
            this.goBackward();
            this.mUpdateStartTimer = 0;
            return;
        }
        if (!fwd && maxspd) {
            this.mUpdatesPerFrame = -MIN_UPDATES_PER_FRAME;
            this.goBackward();
            this.mUpdateStartTimer = 0;
            return;
        }

    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        if (isMouseTarget()) {
            KeyEvent ke = kei.getKeyEvent();
            if (ke.getID() == KeyEvent.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (ch == 'f') {
                    checkMoveStart(true,false);
                    return true;
                }
                if (ch == 'F') {
                    checkMoveStart(true,true);
                    return true;
                }
                if (ch == 'b') {
                    checkMoveStart(false,false);
                    return true;
                }
                if (ch == 'B') {
                    checkMoveStart(false,true);
                    return true;
                }
            }
            return super.handleKeyboardEventHere(kei);
        }
        return false;
    }


}
