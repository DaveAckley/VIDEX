package com.putable.videx.core.oio.load;

import com.putable.videx.core.oio.load.AST.ASTObj;
import com.putable.videx.core.oio.load.AST.ASTSlideDeck;
import com.putable.videx.core.oio.load.AST.ASTTree;

public class Checker {
    private ASTSlideDeck mSlides;
    private final OIOCompiler mCompiler;
    //private HashMap<Integer, ASTObj> mObjects = new HashMap<Integer, ASTObj>();

    private void checkLinks() {
        for (ASTTree t: mSlides) {
            if (t == null)
                continue;
            for (ASTObj o : t) {
                o.checkLinks(mCompiler);
            }
        }
    }


    
    private void checkTypes() {
        mCompiler.warn("Checker.checkTypes: Deimplemented");
        /*
        for (ASTObj s : mSlides) {
            s.instantiateRecursively(mCompiler);
        }
        */
    }

    private void checkObjects() {
//        buildMap();
        checkTypes();
        checkLinks(); // Once all slides are in, all links should be good
    }

    public Checker(OIOCompiler compiler, ASTSlideDeck tree) {
        this.mCompiler = compiler;
        this.mSlides = tree;
        checkObjects();
    }
}
