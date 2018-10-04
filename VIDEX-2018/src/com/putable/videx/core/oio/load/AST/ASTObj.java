package com.putable.videx.core.oio.load.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.Parser;
import com.putable.videx.core.oio.load.TokType;
import com.putable.videx.core.oio.load.Token;
import com.putable.videx.interfaces.OIOAble;

public class ASTObj extends ASTNode implements Iterable<ASTDef> {
    private int mOnum = -1;
    private String mType = null;
    private LinkedList<ASTDef> mDefs = new LinkedList<ASTDef>();
    private OIOAble mOIOInstance = null;

    @Override
    public String toString() {
        return "ao("+mOnum+", "+mType+", "+mOIOInstance+")";
    }
    
    public ASTValue getASTValue(String dmname) {
        for (ASTDef ad : this) {
            if (ad.getName().equals(dmname)) {
                return ad.getValue();
            }
        }
        return null;
    }
    
    public Double checkForDefNumeric(String dmname) {
        for (ASTDef ad : this) {
            if (ad.getName().equals(dmname)) {
                ASTValue av = ad.getValue();
                Token t = av.getToken();
                if (t.toktype != TokType.NUM)
                    throw new ParseException(t, "Expected number for " + dmname + " in " + this);
                return t.nval;
            }
        }
        return null;
    }

    public String checkForDefString(String dmname) {
        for (ASTDef ad : this) {
            if (ad.getName().equals(dmname)) {
                ASTValue av = ad.getValue();
                Token t = av.getToken();
                if (t.toktype != TokType.STRING)
                    throw new ParseException(t, "Expected string for " + dmname + " in " + this);
                return t.sval;
            }
        }
        return null;
    }

    public Integer checkForDefOnum(String dmname) {
        for (ASTDef ad : this) {
            if (ad.getName().equals(dmname)) {
                ASTValue av = ad.getValue();
                Token t = av.getToken();
                if (t.toktype != TokType.ONUM)
                    throw new ParseException(t, "Expected ONUM for " + dmname + " in " + this);
                return t.oval;
            }
        }
        return null;
    }
/*
    public void configureOIOAbleInstance(OIOAbleGlobalMap refs) {
        if (mOIOInstance != null)
            mOIOInstance.configureSelf(this,refs);
    }
  */  
    public void setOIOAbleInstance(OIOAble oio) {
        if (oio == null) throw new IllegalArgumentException();
        if (mOIOInstance != null) throw new IllegalStateException();
        mOIOInstance = oio;
    }
    
    public OIOAble getOIOAbleInstance() {
        return mOIOInstance;
    }

    public Class<?> getDescribedClass() {
        try {
            return this.getClass().getClassLoader().loadClass(this.getType());
        } catch (ClassNotFoundException e) {
            throw new ParseException(this.getToken(),
                    "Class not found: " + this.getType());
        }
    }
    
    @Override
    public void checkLinks(OIOCompiler c) {
        if (c.get(mOnum) == null)
            throw new IllegalStateException("My onum not in set #" + mOnum);
        for (ASTDef d : mDefs) 
            d.checkLinks(c);
    }
    
    public int getOnum() {
        return mOnum;
    }

    public ASTValue getValueOfMember(String memberName) {
        for (ASTDef ad : mDefs) {
            if (memberName.equals(ad.getName())) return ad.getValue();
        }
        return null;
    }
    
    public String getType() {
        return mType;
    }
    
    public void setOnum(int onum) {
        if (mOnum >= 0)
            throw new IllegalStateException();
        if (onum < 0)
            throw new IllegalArgumentException();
        mOnum = onum;
    }

    public void setType(String type) {
        if (mType != null)
            throw new IllegalStateException();
        if (type == null)
            throw new IllegalArgumentException();
        mType = type;
    }

    public void addDef(ASTDef def) {
        if (def == null)
            throw new NullPointerException();
        mDefs.add(def);
    }

    public static ASTObj parse(Parser p) {
        Token onum = p.require(TokType.ONUM);
        int onumval = onum.oval;
        p.require(TokType.COLON);
        if (onumval <= 0)
            throw new ParseException(onum, "Illegal ONUM " + onumval);

        Token type = p.require(TokType.ID);
        
        p.require(TokType.OPEN_CURLY);

        ASTObj obj = new ASTObj();
        obj.setToken(onum);
        obj.setOnum(onumval);
        obj.setType(type.sval);
        while (true) {
            Token next = p.next();
            if (next.toktype == TokType.CLOSE_CURLY)
                break;
            p.pushBack();
            ASTDef def = ASTDef.parse(p);
            obj.addDef(def);
        }
        return obj;
    }

    @Override
    public void write(Writer w) throws IOException {
        w.write("#" + this.mOnum + ":" + this.mType + " {\n");
        for (ASTDef d : this.mDefs) {
            d.write(w);
        }
        w.write("}\n\n");
    }

    @Override
    public Iterator<ASTDef> iterator() {
        return mDefs.listIterator();
    }

}
