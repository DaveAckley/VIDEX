package com.putable.videx.std.vo;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public abstract class ReadOnlyDirectoryManager extends EventAwareVO {

    public abstract Predicate<Path> getPredicate() ;
    
    @OIO
    private Path mDirectory = null;
    private Path mLastLoadedDirectory = null;

    private int mGenerationNumber = 0;

    private List<Path> mPaths = new LinkedList<Path>();

    public class RODMIterator implements ListIterator<Path> {
        private int mMyGeGeGeneration = mGenerationNumber;
        private ListIterator<Path> mIterator = mPaths.listIterator();

        public boolean isValid() {
            return mMyGeGeGeneration == mGenerationNumber;
        }

        public void ensureValid() {
            if (!isValid())
                throw new IllegalStateException();
        }

        @Override
        public boolean hasNext() {
            ensureValid();
            return mIterator.hasNext();
        }

        @Override
        public Path next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return mIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            ensureValid();
            return mIterator.hasPrevious();
        }

        @Override
        public Path previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            return mIterator.previous();
        }

        @Override
        public int nextIndex() {
            if (!hasNext())
                throw new NoSuchElementException();
            return mIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            if (!hasPrevious())
                throw new NoSuchElementException();
            return mIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Path e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Path e) {
            throw new UnsupportedOperationException();
        }
    }

    public RODMIterator getIterator() {
        return new RODMIterator();
    }

    public void setDirectory(Path dir) {
        mDirectory = dir;
    }

    public void loadFiles() {
        if (mDirectory == null)
            throw new IllegalStateException();
        File[] files = mDirectory.toFile().listFiles();
        Arrays.sort(files);
        mPaths.clear();
        mGenerationNumber++;
        Predicate<Path> pred = this.getPredicate();
        for (final File fileEntry : files) {
            Path p = fileEntry.toPath();
            if (fileEntry.isFile() && pred.test(p)) {
                mPaths.add(p);
            }
        }
        mLastLoadedDirectory = mDirectory;
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        if (mDirectory == mLastLoadedDirectory)
            return true; // up to date
        if (mDirectory == null)
            return true; // can't init yet
        loadFiles();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        v2d.getGraphics2D().fillRect(0, 0, 100, 100);
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }
}
