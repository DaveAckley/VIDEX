package com.putable.videx.core.oio.load.AST;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.ParseException;

public class ASTSlideDeck extends ASTNode implements Iterable<ASTTree> {
    private final String mDeckDir;
    private FileTime mDeckDirTimestamp;
    // private ASTCSS mCSS = null;
    private Map<String, ASTTree> mFileTrees = new TreeMap<String, ASTTree>();

    public void addFileTree(String filename, ASTTree tree) {
        if (tree == null) throw new IllegalArgumentException();
        ASTTree t = mFileTrees.get(filename);
        if (t != null)
            throw new ParseException(tree.getToken(), "Duplicate file tree " + t.getToken());
        mFileTrees.put(filename, tree);
    }

    public ASTSlideDeck(String baseDir) {
        mDeckDir = baseDir;
    }

    public ASTTree getSlide(String name) {
        ASTTree ret = mFileTrees.get(name);
        if (ret == null) {
            throw new IllegalStateException("no slide for " + name);
            // ret = new ASTSlide(name);
            // mSlides.put(name, ret);
        }
        return ret;
    }

    @Override
    public void write(Writer w) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Iterator<ASTTree> iterator() {
        return mFileTrees.values().iterator();
    }

    @Override
    public void checkLinks(OIOCompiler c) {
        for (ASTTree s : this) {
            s.checkLinks(c);
        }

    }

    /**
     * Make the 'real' SlideDeck from the AST description of a SlideDeck
     * @param map where to look up onums
     * @param css the css for the whole slidedeck
     * @param slideHTML map from slide name to associated html if any
     * @return the generated SlideDeck
     */
    /*
    public SlideDeck generateSlideDeck(OIOAbleGlobalMap map, String css,
            Map<String, String> slideHTML) {
        SlideDeck sd = new SlideDeck(mDeckDir);
        if (css != null)
            sd.setCSS(css);
        for (ASTTree t : this) {
            for (ASTObj o : t) {
                //o.configureOIOAbleInstance(map);
                OIOAble instance = o.getOIOAbleInstance();
                if (!(instance instanceof Slide)) {
                    throw new ParseException(o.getToken(), "First OIO not Slide");
                }
                Slide slide = (Slide) instance;
                String name = slide.getSlideName();
                if (name == null)
                    throw new ParseException(o.getToken(), "Unnamed Slide " + slide);
                String html = slideHTML.get(name);
                if (html != null)
                    slide.theHTML(html);
                if (slide instanceof StandardVO) {
                    sd.addPendingChild((StandardVO) slide);
                } else
                    throw new ParseException(o.getToken(), "Slide not VO??");
                break; // Just looking at first one in each tree, ugh.
            }
        }
        return sd;
    }
*/
}
