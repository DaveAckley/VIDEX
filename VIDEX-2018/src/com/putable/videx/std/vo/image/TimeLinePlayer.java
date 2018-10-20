package com.putable.videx.std.vo.image;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public class TimeLinePlayer extends ImageViewer {

    @OIO
    private Path mImageDirectory = Paths.get("");
    private Path mLastLoadedDirectory = null;
    
    private List<Path> mImageFiles = new LinkedList<Path>();
    private ListIterator<Path> mIterator = mImageFiles.listIterator();

    public void setImageDirectory(Path dir) {
        mImageDirectory = dir;
    }
    
    private void loadFilesIfNeeded() {
        if (mImageDirectory.equals(mLastLoadedDirectory)) return;
        if (mImageDirectory == null) throw new IllegalStateException();
        File[] files = mImageDirectory.toFile().listFiles();
        Arrays.sort(files);
        mImageFiles.clear();
        for (final File fileEntry : files) {
            if (fileEntry.isFile()) {
                String name = fileEntry.getName();
                if (name.endsWith(".png")) // for now, just..
                    mImageFiles.add(fileEntry.toPath());
            }
        }
        mIterator = mImageFiles.listIterator();
        mLastLoadedDirectory = mImageDirectory;
    }

    @OIO
    private int mUpdatesThisFrame = 0;
    
    @OIO
    private int mUpdatesPerFrame = 2;
    
    @Override
    public boolean updateThisVO(Stage stage) {
        loadFilesIfNeeded();
        if (++mUpdatesThisFrame >= mUpdatesPerFrame) {
            this.goForward();
            mUpdatesThisFrame = 0;
        }
        return super.updateThisVO(stage);
    }

    @Override
    public ListIterator<Path> pathPosition() {
        return mIterator;
    }
}
