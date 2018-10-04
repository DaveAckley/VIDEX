package com.putable.videx.std.vo.WildBits;

public abstract class RunningWildBitOp implements WildBitOp {
    @Override
    public int getMinArity() {
        return 1;
    }

    @Override
    public int getMaxArity() {
        return Integer.MAX_VALUE;
    }

    public abstract WildBitValue performUnaryOp(WildBitValue val) ;

    public abstract WildBitValue performRunningOp(WildBitValue acc, WildBitValue val) ;

    @Override
    public WildBitValue performOp(WildBitValue... values) {
        int len = values.length;
        if (len < getMinArity() || len > getMaxArity())
            throw new IllegalArgumentException();
        if (len == 0) return WildBitValue.BLANK;
        WildBitValue ret = performUnaryOp(values[0]);
        for (int i = 1; i < len; ++i)
            ret = performRunningOp(ret, values[i]);
        return ret;
    }

}
