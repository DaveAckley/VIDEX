package com.putable.videx.std.vo.WildBits;


public class WildBitOpUNION extends RunningWildBitOp {

    @Override
    public String getOpName() {
        return "OR";
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

        // If no ILLEGAL, WILD dominates
        if (acc == WildBitValue.WILD || val == WildBitValue.WILD)
            return WildBitValue.WILD;
        
        // If no ILLEGAL or WILD, UNKNOWN dominates
        if (acc == WildBitValue.UNKNOWN || val == WildBitValue.UNKNOWN)
            return WildBitValue.UNKNOWN;
        
        // If no ILLEGAL, WILD, or UNKNOWN, match dominates
        if (acc == val) return acc;
        
        // Otherwise we have a ZERO and a ONE
        return WildBitValue.WILD;
    }


}