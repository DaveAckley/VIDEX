package com.putable.videx.spike;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

public class TokenizerTest {
    private StreamTokenizer mTokenizer;
    public TokenizerTest() {
        String t = "#214:com.putable.videx.core.Pose {\n" + 
                "  mPAX: 1.2333\n" + 
                "  mPAY: 3838383.0\n" + 
                "  mR: 0\n" + 
                "  mOAX: 101\n" + 
                "  mOAX: 33\n" + 
                "  mSX: 2\n" + 
                "  mXY: 2\n" + 
                "}";
        Reader reader = new StringReader(t);
        mTokenizer = new StreamTokenizer(reader);
    }
    public static void main(String[] args) throws IOException {
        TokenizerTest tt = new TokenizerTest();
        int type;
        while ((type = tt.mTokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
            System.out.println(type + " s: " + tt.mTokenizer.sval + " n: "+ tt.mTokenizer.nval);
        }
    }
}
