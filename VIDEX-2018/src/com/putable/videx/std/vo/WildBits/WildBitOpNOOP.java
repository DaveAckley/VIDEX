package com.putable.videx.std.vo.WildBits;

public class WildBitOpNOOP extends RunningWildBitOp {

    @Override
    public String getOpName() {
        return "NOOP";
    }

    @Override
    public int getMaxArity() {
        return 1;
    }

    @Override
    public WildBitValue performUnaryOp(WildBitValue val) {
        return val; // For all values
    }

    @Override
    public WildBitValue performRunningOp(WildBitValue acc, WildBitValue val) {
        throw new IllegalStateException();
    }

}