package com.putable.videx.std.vo.WildBits;

public interface WildBitOp {
    String getOpName();
    int getMinArity();
    int getMaxArity();
    WildBitValue performOp(WildBitValue... values);
}
