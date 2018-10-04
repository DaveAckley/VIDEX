package com.putable.videx.spike;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import com.putable.videx.core.oio.load.OIOCompiler;
import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.AST.ASTTree;

public class TTTEST3 {
    
    public static void main(String[] args ) {
        String data = "#1:com.putable.videx.core.StandardVO {}/*clams*/#213:com.putable.videx.std.vo.WiggleSquare {\n" + 
                "  mPose: #214 // And no foggin toher\n" + 
                "  mScaleScale: 0.1\n" + 
                "  mScale: 1\n" + 
                "  mXvel: 0\n" + 
                "  mYvel: 0\n" + 
                "  mSvel: 1\n" + 
                "  mRvel: 0\n" + 
                "  mBang: \"(\\b\\n\\r\\t\\f) bong\" " +
                "  mGratuitousString: \"hi \\nho \\\"batman\\\" slash\\\\ \u0321clams\"\n" +
                "  mCopyGrat: \"zi \\000ho \\\"batman\\\" slash\\\\ ̡clams\"" +
                "  mGrpcodat: \"zi \\000ho \\\"batman\\\" slash\\\\ ̡clams\"" +
                "  mCop2iiii: \"hi ho \\\"batman\\\" slash\\\\ ̡clams\"" +
                "  mCop3iiii: \"hi \\nho \\\"batman\\\" slash\\\\ ̡clams\"" +
                "}\n" + 
                "\n" + 
                "#214:com.putable.videx.core.Pose {\n" + 
                "  mPAX: 1.2333\n" + 
                "  mPAY: 3838383.0\n" + 
                "  mR: 0\n" + 
                "  mOAX: 101\n" + 
                "  mOAX: 33\n" + 
                "  mSX: 2\n" + 
                "  mXY: 2\n" + 
                "}\n" + 
                "\n" + 
                "#210:com.putable.videx.std.vo.UnitAxes {\n" + 
                "  mVO: [ #19 #212 ]\n" + 
                "  mVO: [ 1 [ 2 [ 3 [ 4 5 ] 6 ] 7 ] #9 ]\n" + 
                "}";
        StringReader r = new StringReader(data);
        OIOCompiler compiler = new OIOCompiler("foodon'tusemeanymorei'mrotted",null,null);
        ASTTree tree = compiler.compile("testfile", r);

        PrintWriter w = new PrintWriter(System.out);
        try {
            tree.write(w);
            w.flush();
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException io) {
            // TODO Auto-generated catch block
            io.printStackTrace();
        }
    }
}
