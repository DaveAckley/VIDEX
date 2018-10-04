package com.putable.videx.std.vo.WildBits;


public class WildBitOpINTERSECTION extends RunningWildBitOp {

    @Override
    public String getOpName() {
        return "AND";
    }

    @Override
    public WildBitValue performUnaryOp(WildBitValue val) {
        return val;
    }

    @Override
    public WildBitValue performRunningOp(WildBitValue acc, WildBitValue val) {
        // BLANK is noop
        if (acc == WildBitValue.BLANK) return val;
        if (val == WildBitValue.BLANK) return acc;

        // ILLEGAL dominates
        if (acc == WildBitValue.ILLEGAL || val == WildBitValue.ILLEGAL)
            return WildBitValue.ILLEGAL;

        // If no ILLEGAL, WILD is noop
        if (acc == WildBitValue.WILD) return val;
        if (val == WildBitValue.WILD) return acc;
        
        // If no ILLEGAL or WILD, UNKNOWN dominates
        if (acc == WildBitValue.UNKNOWN || val == WildBitValue.UNKNOWN)
            return WildBitValue.UNKNOWN;
        
        // If no ILLEGAL, WILD, or UNKNOWN, match dominates
        if (acc == val) return acc;
        
        // Otherwise we have a ZERO and a ONE
        return WildBitValue.ILLEGAL;
    }


}