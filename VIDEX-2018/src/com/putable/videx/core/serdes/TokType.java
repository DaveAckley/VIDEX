package com.putable.videx.core.serdes;

import java.io.StreamTokenizer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum TokType {
    EOF(StreamTokenizer.TT_EOF)
    ,ID(StreamTokenizer.TT_WORD)
    ,NUM(StreamTokenizer.TT_NUMBER)
    ,STRING('"')
    ,COLON(':')
    ,OPEN_CURLY('{')
    ,CLOSE_CURLY('}')
    ,OPEN_SQUARE('[')
    ,CLOSE_SQUARE(']')
    ,ONUM('#')
    ,ERROR(Integer.MIN_VALUE)
    ;
    
    private static final Map<Integer,TokType> mTokenMap = Collections.unmodifiableMap(initMap());
    private static Map<Integer,TokType> initMap() {
        Map<Integer,TokType> map = new HashMap<Integer,TokType>();
        for (TokType tt : TokType.values()) {
            map.put(tt.tokenType, tt);
        }
        return map;
    }
    public static TokType getTokType(int streamTokenType) {
        return mTokenMap.get(streamTokenType);
    }
    public final int tokenType;
    public boolean is(Token token) {
        return this == token.toktype;
    }

    private TokType(int ttype) {
        this.tokenType = ttype;
    }
}