package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class ASTSlide extends ASTNode {
    private final String mSlideName;
    private String mHTML = null;
    private ASTTree mObjs = null;
    
    public ASTTree getASTTree() { return mObjs; }

    public String getHTML() { return mHTML; }
    
    public void setHTML(String html) {
        if (mHTML != null) throw new IllegalStateException();
        if (html == null) throw new NullPointerException();
        mHTML = html;
    }

    public void setASTTree(ASTTree tree) {
        if (mObjs != null) throw new IllegalStateException();
        if (tree == null) throw new NullPointerException();
        mObjs = tree;
    }

    public String getName() { return mSlideName; }
    
    public ASTSlide(String name) {
        mSlideName = name;
    }
    
    @Override
    public void write(Writer w) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkLinks(Set<Integer> definedOnums) {
        if (this.mObjs != null)
            this.mObjs.checkLinks(definedOnums);
        
    }

}
