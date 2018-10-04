package com.putable.videx.core.serdes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import com.putable.videx.core.serdes.AST.ASTObj;
import com.putable.videx.core.serdes.AST.ASTSlide;
import com.putable.videx.core.serdes.AST.ASTSlideDeck;
import com.putable.videx.core.serdes.AST.ASTTree;
import com.putable.videx.interfaces.SerDesAble;
import com.putable.videx.interfaces.SerDesAbleGlobalMap;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.utils.FileUtils;

public class Compiler {
    private int mWarnings = 0;
    private int mErrors = 0;
    private Map<Integer,SerDesAble> mSDAMap = new TreeMap<Integer,SerDesAble>();
    public SerDesAbleGlobalMap mSDAGlobalMap = new SerDesAbleGlobalMap() {
        @Override
        public SerDesAble get(int onum) {
            return Compiler.this.get(onum);
        }
    };
    

    public void put(SerDesAble sda) {
        int onum = sda.getOnum();
        if (onum <= 0) throw new IllegalArgumentException();
        if (mSDAMap.get(onum) != null) throw new IllegalStateException();
        mSDAMap.put(onum, sda);
    }
    
    public SerDesAble get(int onum) {
        if (onum <= 0) throw new IllegalArgumentException();
        return mSDAMap.get(onum);
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

    private final String mSlideDeckDir;
    private final ASTSlideDeck mSlideDeck;
    private final Stage mStage;

    public ASTSlideDeck getSlideDeck() {
        return mSlideDeck;
    }

    public Compiler(String deckDir, Stage forStage) {
        mSlideDeckDir = deckDir;
        mSlideDeck = new ASTSlideDeck(deckDir);
        mStage = forStage;
    }

    private void loadFile(File file) {
        String name = file.getName();
        if (name.endsWith(".css"))
            loadCSS(file);
        else if (name.endsWith(".html"))
            loadHTMLSlide(file);
        else if (name.endsWith(".sda"))
            loadSDAFile(file);
        else if (name.endsWith("~"))
            return;
        else
            warn("Skipped unrecognized file '" + file.getAbsolutePath() + "' in directory "
                    + mSlideDeckDir);
    }

    private String getNameWithoutExtension(String name) {
        int lastdot = name.lastIndexOf('.');
        if (lastdot < 0) throw new IllegalArgumentException(name + " does not have an extension");
        return name.substring(0, lastdot);
    }

    private void loadSDAFile(File sdaFile) {
        String fileName = sdaFile.getName();
        String slideName = getNameWithoutExtension(fileName);
        ASTTree at = compileSDA(sdaFile);
        msg("Loaded SDA for  " + slideName);
        ASTSlide as = this.mSlideDeck.getSlide(slideName);
        as.setASTTree(at);
    }

    private void loadHTMLSlide(File htmlFile) {
        String fileName = htmlFile.getName();
        String slideName = getNameWithoutExtension(fileName);
        String guts = FileUtils.readWholeFile(htmlFile.toPath());
        if (guts != null) {
            ASTSlide as = this.mSlideDeck.getSlide(slideName);
            msg("Loaded HTML for " + slideName);
            as.setHTML(guts);
        }
    }

    private File mCSSFile = null;
    private String mCSS = "";

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

    public void reload() {
        // System.out.println("RELOAD " + mDeckDir);
        try {
            loadDirFiles(Paths.get(mSlideDeckDir).toFile());
            checkLoadedFiles(); // This now instantiates SDAs
            //instantiateVOs();
            configureSDAs();
            replaceRoot();
        }
        catch (ParseException pe) {
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
        VO vo = mStage.getRoot();

        throw new UnsupportedOperationException("USE STAGE "+mStage);
    }

    private void configureSDAs() {
        for (ASTSlide s : mSlideDeck) {
            ASTTree t = s.getASTTree();
            if (t == null)
                continue;
            for (ASTObj o : t) {
                o.configureSerDesAbleInstance(this.mSDAGlobalMap);
            }
        }
    }

    private void checkLoadedFiles() {
        Checker c = new Checker(this, this.mSlideDeck);
        System.out.println(this.mSlideDeck);
    }

    private void instantiateVOs() {
/*
        for (ASTSlide s : mSlideDeck) {
            ASTTree t = s.getASTTree();
            if (t == null)
                continue;
            for (ASTObj o : t) {
                Class<?> c = getClass(o);
                if (!findInterface(SerDesAble.class, c)) {
                        throw new ParseException(o.getToken(),
                                "Class not SerDes: " + o.getType());
                    }

                    System.out.println(
                            "#" + o.getOnum() + " " + c.getName() + " SerDes OK");
                }
            }
        }
        */
    }

    public ASTTree compileSDA(File sdaFile) {
        try {
            Reader fr = new FileReader(sdaFile);
            return compile(sdaFile.toString(), fr);
        }
        catch (FileNotFoundException fe) {
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
