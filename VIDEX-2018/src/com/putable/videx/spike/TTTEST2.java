package com.putable.videx.spike;

import java.io.StringReader;

import com.putable.videx.core.oio.load.Lexer;
import com.putable.videx.core.oio.load.TokType;
import com.putable.videx.core.oio.load.Token;

public class TTTEST2 {
    public static void main(String[] args ) {
        String data = "/*clams*/#213:com.putable.videx.std.vo.WiggleSquare {\n" + 
                "  mPose: #214 // And no foggin toher\n" + 
                "  mScaleScale: 0.1\n" + 
                "  mScale: 1\n" + 
                "  mXvel: 0\n" + 
                "  mYvel: 0\n" + 
                "  mSvel: 1\n" + 
                "  mRvel: 0\n" + 
                "  mGratuitousString: \"hi ho \\\"batman\\\" \u0321clams\"\n" + 
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
                "#233:com.putable.videx.std.vo.UnitAxes {\n" + 
                "  mVO: [ #19 #212 ]\n" + 
                "}";
        StringReader r = new StringReader(data);
        Lexer lex = new Lexer("testfile", r);
        Token t;
        while ((t = lex.nextToken()) != null) {
            System.out.println(t.toString());
            if (t.toktype == TokType.EOF) break;
        }
    }
}
