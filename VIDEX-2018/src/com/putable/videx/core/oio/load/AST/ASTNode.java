package com.putable.videx.core.oio.load.AST;

import java.io.IOException;
import java.io.Writer;

import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.Token;

public abstract class ASTNode {
    private Token mToken = null;

    public void setToken(Token t) {
        mToken = t;
    }

    public Token getToken() {
        return mToken;
    }

    public abstract void write(Writer w) throws IOException;

    public abstract void checkLinks(OIOCompiler compiler);
}
