package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.putable.videx.core.serdes.Token;

public abstract class ASTNode {
    private Token mToken = null;

    public void setToken(Token t) {
        mToken = t;
    }

    public Token getToken() {
        return mToken;
    }

    public abstract void write(Writer w) throws IOException;

    public abstract void checkLinks(Set<Integer> definedOnums);
}
