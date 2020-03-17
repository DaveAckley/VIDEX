package com.putable.videx.std.vo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Predicate;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.RewriterEH;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public abstract class ReadOnlyDirectoryManager extends EventAwareVO {

    public abstract Predicate<Path> getPredicate() ;
    
    @OIO
    private Path mDirectory = null;
    private Path mLastLoadedDirectory = null;
    private SortedMap<String,String> mPersistenceMap = null;
    private boolean mPersistenceMapNeedsSave = false;
    
    private int mGenerationNumber = 0;

    private ArrayList<Path> mPaths = new ArrayList<Path>();

    public String getPersistentDataFor(Path p) {
        if (mPersistenceMap == null) return null;
        return mPersistenceMap.get(p.toString());
    }

    public boolean updatePersistentDataFor(Path p, String data) {
        if (mPersistenceMap == null) return false;
        String existing = getPersistentDataFor(p);
        if (existing != data) {
            mPersistenceMap.put(p.toString(), data);
            mPersistenceMapNeedsSave = true;
        }
        return true;
    }
    
    public int getPathCount() { return mPaths.size(); }
    public Path getPath(int index) { 
        if (index < 0 || index >= getPathCount()) return null;
        return mPaths.get(index); 
    }

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
            Path ret = mIterator.next();
            System.out.println("NEXTTO "+ret);
            return ret;
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
            Path ret = mIterator.previous();
            System.out.println("PREV TO "+ret);
            return ret;
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
        if (files == null)
            throw new IllegalStateException("Bad directory: "+mDirectory);
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
        checkForPersistentState(mLastLoadedDirectory);
    }

    private void savePersistentState(Path dir) {
        if (mPersistenceMap == null) return; // nothing to do (yet)
        File stateFile = dir.resolve(".persistentState.dat").toFile();
        FileWriter fw;
        try {
            fw = new FileWriter(stateFile.getAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            for (Entry<String, String> kv : this.mPersistenceMap.entrySet()) {
                bw.append(mEscaper.rewrite(kv.getKey()));
                bw.append("=");
                bw.append(mEscaper.rewrite(kv.getValue()));
                bw.newLine();
            }
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void checkForPersistentState(Path dir) {
        if (this.mPersistenceMap != null)
            this.mPersistenceMap.clear();
        else
            this.mPersistenceMap = new TreeMap<String,String>();
        
        File stateFile = dir.resolve(".persistentState.dat").toFile();
        FileReader fr;
        try {
            fr = new FileReader(stateFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            return; // Nothing to do
        }

        BufferedReader br = new BufferedReader(fr);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                processPersistenceLine(line);
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            // Just effen leak descriptors if the map is effdup
        }
    }

    private boolean processPersistenceLine(String line) {
        String[] tv = line.split("=");
        if (tv.length != 2) return persistenceFail("Bad split");
        this.mPersistenceMap.put(mDeescaper.rewrite(tv[0]), mDeescaper.rewrite(tv[1]));
        return true;
    }

    private RewriterEH mEscaper = new RewriterEH("([^-+_.,:;a-zA-Z0-9])")
    {
      public String replacement()
      {
        String matched = group(1);
        if (matched.length() != 1) throw new IllegalStateException();
        int chval = matched.codePointAt(0);
        return String.format("<%d>", chval);
      }
    };

    private RewriterEH mDeescaper = new RewriterEH("<(\\d+)>")
    {
      public String replacement()
      {
          int codepoint = Integer.valueOf(group(1));
          return new StringBuilder().appendCodePoint(codepoint).toString();
      }
    };  

    private boolean persistenceFail(String msg) {
        System.err.print(msg);
        return false;
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        if (mDirectory == mLastLoadedDirectory) {
            if (mPersistenceMapNeedsSave) {
                System.err.println("PERSISTING to "+ mLastLoadedDirectory);
                savePersistentState(mLastLoadedDirectory);
                mPersistenceMapNeedsSave = false;
            }
            return true; // up to date
        }
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
