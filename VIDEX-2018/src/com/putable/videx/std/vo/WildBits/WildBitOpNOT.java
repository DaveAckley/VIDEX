package com.putable.videx.std.vo.WildBits;

public class WildBitOpNOT extends RunningWildBitOp {

    @Override
    public String getOpName() {
        return "NOT";
    }

    @Override
    public int getMaxArity() {
        return 1;
    }

    @Override
    public WildBitValue performUnaryOp(WildBitValue val) {
        if (val == WildBitValue.ZERO)
            return WildBitValue.ONE;
        if (val == WildBitValue.ONE)
            return WildBitValue.ZERO;
        return val; // For WILD, ILLEGAL, UNKNOWN, and BLANK
    }

    @Override
    public WildBitValue performRunningOp(WildBitValue acc, WildBitValue val) {
        throw new IllegalStateException();
    }

}