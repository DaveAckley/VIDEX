package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.putable.videx.core.serdes.ParseException;
import com.putable.videx.core.serdes.Parser;
import com.putable.videx.core.serdes.TokType;
import com.putable.videx.core.serdes.Token;
import com.putable.videx.interfaces.SerDesAble;
import com.putable.videx.interfaces.SerDesAbleGlobalMap;

public class ASTObj extends ASTNode implements Iterable<ASTDef> {
    private int mOnum = -1;
    private String mType = null;
    private LinkedList<ASTDef> mDefs = new LinkedList<ASTDef>();
    private SerDesAble mSerDesInstance = null;
    
    public void configureSerDesAbleInstance(SerDesAbleGlobalMap refs) {
        if (mSerDesInstance != null)
            mSerDesInstance.configureSelf(this,refs);
    }
    
    public void setSerDesAbleInstance(SerDesAble sda) {
        if (sda == null) throw new IllegalArgumentException();
        if (mSerDesInstance != null) throw new IllegalStateException();
        mSerDesInstance = sda;
    }
    
    public SerDesAble getSerDesAbleInstance() {
        return mSerDesInstance;
    }
    
    @Override
    public void checkLinks(Set<Integer> onums) {
        if (!onums.contains(mOnum))
            throw new IllegalStateException("My onum not in set #" + mOnum);
        for (ASTDef d : mDefs) 
            d.checkLinks(onums);
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
