package com.putable.videx.core.serdes;

import java.util.HashMap;

import com.putable.videx.core.serdes.AST.ASTObj;
import com.putable.videx.core.serdes.AST.ASTSlide;
import com.putable.videx.core.serdes.AST.ASTSlideDeck;
import com.putable.videx.core.serdes.AST.ASTTree;
import com.putable.videx.interfaces.SerDesAble;

public class Checker {
    private ASTSlideDeck mSlides;
    private final Compiler mCompiler;
    private HashMap<Integer, ASTObj> mObjects = new HashMap<Integer, ASTObj>();

    private void buildMap() {
        for (ASTSlide s : mSlides) {
            ASTTree t = s.getASTTree();
            if (t == null)
                continue;
            for (ASTObj o : t) {
                int onum = o.getOnum();
                if (this.mObjects.get(onum) != null)
                    throw new ParseException(o.getToken(),
                            "Duplicate onum, also defined at\n"
                                    + this.mObjects.get(onum).getToken());
                this.mObjects.put(onum, o);
            }
        }
    }

    private Class<?> getClass(ASTObj o) {
        try {
            return this.getClass().getClassLoader().loadClass(o.getType());
        } catch (ClassNotFoundException e) {
            throw new ParseException(o.getToken(),
                    "Class not found: " + o.getType());
        }
    }

    public static boolean findInterface(Class<?> iface, Class<?> ofclass) {
        Class<?>[] ifaces = ofclass.getInterfaces();
        for (Class<?> i : ifaces) {
            if (i == iface)
                return true;
            if (i.isInterface() && findInterface(iface, i))
                return true;
        }
        Class<?> sup = ofclass.getSuperclass();
        if (sup != null)
            return findInterface(iface, sup);
        return false;
    }

    private void checkLinks() {
        for (ASTSlide s : mSlides) {
            ASTTree t = s.getASTTree();
            if (t == null)
                continue;
            for (ASTObj o : t) {
                o.checkLinks(this.mObjects.keySet());
            }
        }
    }

    private void checkTypes() {
        for (ASTSlide s : mSlides) {
            ASTTree t = s.getASTTree();
            if (t == null)
                continue;
            for (ASTObj o : t) {
                Class<?> c = getClass(o);
                if (!findInterface(SerDesAble.class, c)) {
                    throw new ParseException(o.getToken(),
                            "Class not SerDes: " + o.getType());
                }
                try {
                    SerDesAble sda = (SerDesAble) c.newInstance();
                    o.setSerDesAbleInstance(sda);
                    mCompiler.put(sda);
                }
                catch (Exception e) {
                    System.err.println("Object instantiation failed for "+o+ " class " +c);
                }
            }
        }
    }

    private void checkObjects() {
        buildMap();
        checkTypes();
        checkLinks(); // Once all slides are in, all links should be good
    }

    public Checker(Compiler compiler, ASTSlideDeck tree) {
        this.mCompiler = compiler;
        this.mSlides = tree;
        checkObjects();
    }
}
