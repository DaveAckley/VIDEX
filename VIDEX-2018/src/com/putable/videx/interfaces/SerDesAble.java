package com.putable.videx.interfaces;

import com.putable.videx.core.serdes.AST.ASTObj;

public interface SerDesAble {

    /**
     * Get the (positive) object number uniquely associated with this
     * SerDesAble.
     * 
     * @return the object number, or -1 if no object number has yet been
     *         assigned to this SerDesAble
     */
    int getOnum();

    void configureSelf(ASTObj yourMap, SerDesAbleGlobalMap refs);

}
