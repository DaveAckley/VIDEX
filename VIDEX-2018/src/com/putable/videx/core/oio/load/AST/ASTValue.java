package com.putable.videx.core.oio.load.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.Parser;
import com.putable.videx.core.oio.load.TokType;
import com.putable.videx.core.oio.load.Token;

public class ASTValue extends ASTNode {
    private Token mValue = null;
    private LinkedList<ASTValue> mArray = null;

    public boolean isNull() {
        return (mValue != null) &&
                (mValue.toktype == TokType.ID) &&
                mValue.sval.equalsIgnoreCase("null");
    }
    
    public double getAsDouble() {
        if (mValue.toktype != TokType.NUM) 
            throw new ParseException(mValue, "Need value as double");
        return mValue.nval;
    }

    public int getAsInt() {
        if (mValue.toktype != TokType.NUM) 
            throw new ParseException(mValue, "Need value as int");
        return (int) mValue.nval;
    }

    public boolean getAsBoolean() {
        if (mValue.toktype != TokType.BOOL) 
            throw new ParseException(mValue, "Need value as boolean");
        return mValue.nval != 0 ? true : false;
    }

    public int getAsOnum() {
        if (mValue.toktype != TokType.ONUM) 
            throw new ParseException(mValue, "Need value as onum");
        return mValue.oval;
    }

    public String getAsString() {
        if (mValue.toktype != TokType.STRING) 
            throw new ParseException(mValue, "Need value as String");
        return mValue.sval;
    }

    @Override
    public String toString() {
        if (mValue != null)
            return "av(" + mValue + ")";
        if (mArray != null)
            return "av[" + mArray + "]";
        return "AV()";
    }

    public Token getSingleValue() {
        return mValue;
    }

    public List<ASTValue> getArrayValue() {
        return mArray;
    }

    public void setSingleValue(Token tok) {
        if (mArray != null)
            throw new IllegalStateException();
        if (mValue != null)
            throw new IllegalStateException();
        if (tok == null)
            throw new NullPointerException();
        mValue = tok;
    }

    public void initArrayValueIfNeeded() {
        if (mValue != null)
            throw new IllegalStateException();
        if (mArray == null)
            mArray = new LinkedList<ASTValue>();
    }

    public void addArrayValue(ASTValue val) {
        if (val == null)
            throw new NullPointerException();
        initArrayValueIfNeeded();
        mArray.add(val);
    }

    public static ASTValue parse(Parser p) {
        Token next = p.next();
        ASTValue val = new ASTValue();
        val.setToken(next);
        if (next.toktype == TokType.ONUM || next.toktype == TokType.STRING
                || next.toktype == TokType.NUM || next.toktype == TokType.BOOL) {
            val.setSingleValue(next);
            return val;
        }
        if (next.toktype != TokType.OPEN_SQUARE)
            throw new ParseException(next, "Expected value or list");
        val.initArrayValueIfNeeded();
        while (true) {
            next = p.next();
            if (next.toktype == TokType.CLOSE_SQUARE)
                break;
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
                if (started)
                    w.write(" ");
                a.write(w);
                started = true;
            }
            w.write("]");
        } else
            w.write(this.mValue.asCode());
    }

    @Override
    public void checkLinks(OIOCompiler c) {
        if (this.mValue != null && this.mValue.toktype == TokType.ONUM
                && c.get(this.mValue.oval) == null)
            throw new ParseException(this.mValue, "Undefined reference");
        else if (this.mArray != null)
            for (ASTValue a : this.mArray)
                a.checkLinks(c);
    }
}
