package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.putable.videx.core.serdes.ParseException;
import com.putable.videx.core.serdes.Parser;
import com.putable.videx.core.serdes.TokType;
import com.putable.videx.core.serdes.Token;

public class ASTDef extends ASTNode {
    private String mName = null;
    private ASTValue mValue = null;

    public void setName(String name) {
        if (mName != null)
            throw new IllegalStateException();
        if (name == null)
            throw new IllegalArgumentException();
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setValue(ASTValue v) {
        if (mValue != null)
            throw new IllegalStateException();
        if (v == null)
            throw new IllegalArgumentException();
        mValue = v;
    }

    public ASTValue getValue() {
        return mValue;
    }

    public static ASTDef parse(Parser p) {
        Token next = p.next();
        if (next.toktype != TokType.ID)
            throw new ParseException(next, "Expecting member name (as ID)");
        String name = next.sval;
        p.require(TokType.COLON);
        ASTValue val = ASTValue.parse(p);
        ASTDef def = new ASTDef();
        def.setToken(next);
        def.setName(name);
        def.setValue(val);
        return def;
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write(this.mName + ": ");
        this.mValue.write(w);
        w.write("\n");

    }

    @Override
    public void checkLinks(Set<Integer> definedOnums) {
        if (this.mValue != null)
            this.mValue.checkLinks(definedOnums);
    }

}
