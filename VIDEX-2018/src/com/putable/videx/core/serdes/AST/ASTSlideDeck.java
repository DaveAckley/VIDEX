package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ASTSlideDeck extends ASTNode implements Iterable<ASTSlide> {
    private final String mDeckDir;
    private FileTime mDeckDirTimestamp;
//    private ASTCSS mCSS = null;
    private Map<String, ASTSlide> mSlides = new TreeMap<String, ASTSlide>();

    public ASTSlideDeck(String baseDir) {
        mDeckDir = baseDir;
    }
    
    public ASTSlide getSlide(String name) {
        ASTSlide ret = mSlides.get(name);
        if (ret == null) {
            ret = new ASTSlide(name);
            mSlides.put(name, ret);
        }
        return ret;
    }

    @Override
    public void write(Writer w) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public Iterator<ASTSlide> iterator() {
        return mSlides.values().iterator();
    }

    @Override
    public void checkLinks(Set<Integer> definedOnums) {
        for (ASTSlide s : this.mSlides.values()) {
            s.checkLinks(definedOnums);
        }
        
    }

}
