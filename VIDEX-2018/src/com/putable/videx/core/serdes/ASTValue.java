package com.putable.videx.core.serdes;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Set;

import com.putable.videx.core.serdes.AST.ASTNode;

public class ASTValue extends ASTNode {
    private Token mValue = null;
    private LinkedList<ASTValue> mArray = null;

    public void setSingleValue(Token tok) {
        if (mArray != null)
            throw new IllegalStateException();
        if (mValue != null)
            throw new IllegalStateException();
        if (tok == null)
            throw new NullPointerException();
        mValue = tok;
    }

    public void addArrayValue(ASTValue val) {
        if (mValue != null)
            throw new IllegalStateException();
        if (val == null)
            throw new NullPointerException();
        if (mArray == null)
            mArray = new LinkedList<ASTValue>();
        mArray.add(val);
    }

    public static ASTValue parse(Parser p) { 
        Token next = p.next();
        ASTValue val = new ASTValue();
        if (next.toktype == TokType.ONUM || next.toktype == TokType.STRING || next.toktype == TokType.NUM) {
            val.setSingleValue(next);
            return val;
        }
        if (next.toktype != TokType.OPEN_SQUARE) throw new ParseException(next, "Expected value or list");
        while (true) {
            next = p.next();
            if (next.toktype == TokType.CLOSE_SQUARE) break;
            p.pushBack();
            ASTValue sub = ASTValue.parse(p);
            val.addArrayValue(sub);
        }
        return val;
    }
    
    @Override
    public void write(Writer w) throws IOException {
        if (this.mArray != null) {
            w.write("[");
            boolean started = false;
            for (ASTValue a : this.mArray) {
                if (started) w.write(" ");
                a.write(w);
                started = true;
            }
            w.write("]");
        } else 
            w.write(this.mValue.asCode());
    }

    @Override
    public void checkLinks(Set<Integer> definedOnums) {
        throw new UnsupportedOperationException("REALLY NEEDEDOOO?");
    }
}
