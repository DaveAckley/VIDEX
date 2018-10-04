package com.putable.videx.core.serdes.AST;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.putable.videx.core.serdes.Parser;
import com.putable.videx.core.serdes.TokType;
import com.putable.videx.core.serdes.Token;

public class ASTTree extends ASTNode implements Iterable<ASTObj> {
    private LinkedList<ASTObj> mObjs = new LinkedList<ASTObj>();

    public void addObj(ASTObj o) {
        mObjs.add(o);
    }

    public static ASTTree parse(Parser p) {
        ASTTree tree = null;
        while (true) {
            Token t = p.next();
            if (t.toktype == TokType.EOF)
                return tree;
            p.pushBack();
            ASTObj o = ASTObj.parse(p);
            if (tree == null) {
                tree = new ASTTree();
                tree.setToken(o.getToken());
            }
            tree.addObj(o);
        }
    }

    @Override
    public void write(Writer w) throws IOException {
        for (ASTNode n : this.mObjs) {
            n.write(w);
        }
        w.write("//EOF\n");
    }

    @Override
    public Iterator<ASTObj> iterator() {
        return mObjs.listIterator();
    }

    @Override
    public void checkLinks(Set<Integer> definedOnums) {
        for (ASTObj obj : this)
            obj.checkLinks(definedOnums);
    }

}
