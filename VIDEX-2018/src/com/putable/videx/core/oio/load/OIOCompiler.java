package com.putable.videx.core.oio.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.putable.videx.core.oio.load.AST.ASTObj;
import com.putable.videx.core.oio.load.AST.ASTTree;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.utils.FileUtils;

public class OIOCompiler {
    private int mWarnings = 0;
    private int mErrors = 0;
    private OIOLoad mLoader = null;
    private LinkedList<ASTObj> mToConfigure = new LinkedList<ASTObj>();
    private Map<String, String> mSlideNameToHTMLMap = new HashMap<String, String>();
    public final OIOAbleGlobalMap mOIOAGlobalMap;

    public boolean instantiateIfNeeded(ASTObj o) {
        int onum = o.getOnum();
        if (this.get(onum) != null) // Already instantiated
            return false;
        
        Class<?> c = o.getDescribedClass();
        OIOAble newb = mOIOAGlobalMap.newOIOAble(c, onum);

        if (newb == null)
            this.error("Object instantiation failed for " + o + " class " + c);
        else {
            o.setOIOAbleInstance(newb);
            this.mToConfigure.add(o);
        }
        return true;
    }

    /*
     * Iterate through the oio fields and configure from o's ASTDefs, plus
     * mLoader's external content where needed
    private void configureOIOInstance(ASTObj o) {
        mLoader.configureOIOAble(o);
    }
     */

    public void put(OIOAble oio, int onum) {
        throw new IllegalStateException("DEIMPLEMENTOS: No put, use factory create");
/*
        if (onum <= 0)
            throw new IllegalArgumentException("Bad onum " + onum);
        if (oio == null)
            throw new IllegalArgumentException("Null oio");

        if (this.mOIOAGlobalMap.get(onum) != null)
            throw new IllegalStateException();
        mOIOAMap.put(onum, oio);
*/
    }

    public OIOAble get(int onum) {
        if (onum <= 0)
            throw new IllegalArgumentException();
        return mOIOAGlobalMap.get(onum);
    }

    public void msg(String msg) {
        System.err.println("MESSAGE: " + msg);
    }

    public void warn(String msg) {
        System.err.println("WARN: " + msg);
        ++mWarnings;
    }

    public void error(String msg) {
        System.err.println("ERROR: " + msg);
        ++mErrors;
    }

    private final String mBaseDirectory;
    public String getBaseDirectory() { return mBaseDirectory; }
    
    public OIOCompiler(String baseDir, OIOAbleGlobalMap map, OIOLoad loader) {
        mOIOAGlobalMap = map;
        mLoader = loader;
        mBaseDirectory = baseDir;
    }

    public void loadFile(File file) {
        String name = file.getName();
        if (name.endsWith(".css"))
            loadCSS(file);
        else if (name.endsWith(".html"))
            loadHTMLSlide(file);
        else if (name.endsWith(".oio"))
            loadOIOFile(file);
        else if (name.endsWith("~"))
            return;
        else
            warn("Skipped unrecognized file '" + file.getAbsolutePath()
                    + "' in directory " + mBaseDirectory);
    }

    private String getNameWithoutExtension(String name) {
        int lastdot = name.lastIndexOf('.');
        if (lastdot < 0)
            throw new IllegalArgumentException(
                    name + " does not have an extension");
        return name.substring(0, lastdot);
    }

    // Parse and instantiate all OIOs in file
    private void loadOIOFile(File oioFile) {
        String fileName = oioFile.getName();
        String slideName = getNameWithoutExtension(fileName);
        ASTTree at = compileOIO(oioFile);
        if (at == null) {
            warn("File gone: " + oioFile);
            return;
        }
        for (ASTObj ao : at) {
            if (!this.instantiateIfNeeded(ao))
                throw new ParseException(ao.getToken(),
                        "Duplicate instantiation ");
        }
        msg("Loaded OIO for  " + slideName);
    }

    private void loadHTMLSlide(File htmlFile) {
        String fileName = htmlFile.getName();
        String slideName = getNameWithoutExtension(fileName);
        String guts = FileUtils.readWholeFile(htmlFile.toPath());
        if (guts != null) {
            if (this.mSlideNameToHTMLMap.get(slideName) != null)
                this.warn("Multiple HTML files found for " + slideName);
            this.mSlideNameToHTMLMap.put(slideName, guts);
            msg("Loaded HTML for " + slideName);
        }
    }

    private File mCSSFile = null;
    private String mCSS = "";

    public String getCSS() {
        return mCSS;
    }

    private void loadCSS(File cssFile) {
        String guts = FileUtils.readWholeFile(cssFile.toPath());
        if (guts != null) {
            if (mCSSFile != null) {
                if (!mCSSFile.equals(cssFile)) {
                    warn("Replacing CSS from " + mCSSFile + " with that from "
                            + cssFile);
                }
            }
            mCSSFile = cssFile;
            mCSS = guts;
            // StyleSheet s = mKit.getStyleSheet();
            // s.addRule(guts);
            // System.out.println("STYLE " + guts + " -> " + s);
            // mKit.setStyleSheet(s);
        }
    }

    private void loadDirFiles(final File dir) {
        if (!dir.isDirectory()) {
            error(dir + " is not a directory");
            throw new IllegalArgumentException();
        }
        for (final File fileEntry : dir.listFiles()) {
            if (fileEntry.isDirectory()) {
                loadDirFiles(fileEntry);
            } else {
                loadFile(fileEntry);
                // mSlideDeck.considerLoading(fileEntry);
                // System.out.println(fileEntry.getName() + " -> " +
                // fileEntry.getPath());
            }
        }

    }

    public void reload(OIOLoad loader) {
        try {
            loadDirFiles(Paths.get(mBaseDirectory).toFile()); // This instantiates but does not configure OIOAbles
            configureOIOs();
        } catch (ParseException pe) {
            System.err.println(pe.onToken + ":" + pe.message);
            throw pe;
        }
        /*
         * String cssFile = "default.css"; Path p = Paths.get(mSlideDeckDir,
         * cssFile); String guts = FileUtils.readWholeFile(p); if (guts != null)
         * { //StyleSheet s = mKit.getStyleSheet(); //s.addRule(guts);
         * //System.out.println("STYLE " + guts + " -> " + s);
         * //mKit.setStyleSheet(s); }
         */
    }

    /*
     * @Override public boolean updateThisVO(Stage stage) { FileTime time =
     * FileUtils.getModificationTime(Paths.get(mDeckDir)); if (mDeckDirTimestamp
     * == null || mDeckDirTimestamp.compareTo(time) < 0) { mDeckDirTimestamp =
     * time; reload(); } return false; }
     */

    private void replaceRoot() {
        throw new UnsupportedOperationException("UNIMELM");
        /*VO vo = mStage.getRoot();
        vo.addPendingChild(this.mSlideDeckVO);*/
    }

    public void configureOIOs() {
        ASTObj ao;
        while ((ao = mToConfigure.poll()) != null) {
            this.mLoader.configureOIOAble(ao,this.mOIOAGlobalMap);
        }
    }

/*
    private void checkLoadedFiles() {
        Checker c = new Checker(this, this.mSlideDeck);
        System.out.println(this.mSlideDeck);
    }
*/
    public ASTTree compileOIO(File oioFile) {
        try {
            Reader fr = new FileReader(oioFile);
            return compile(oioFile.toString(), fr);
        } catch (FileNotFoundException fe) {
            // FALL THROUGH
        }
        return null;
    }

    public ASTTree compile(String file, Reader r) {
        Lexer lex = new Lexer(file, r);
        Parser p = new Parser(this, lex);

        try {
            ASTTree tree = p.parse();
            return tree;
        } catch (ParseException pe) {
            System.err.println(pe);
            throw pe;
        }
    }
}
