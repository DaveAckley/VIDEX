package com.putable.videx.std.vo.WildBits;


public class VOBitOpUNION extends WildBitVectorOp {

    private RunningWildBitOp mOp = new WildBitOpUNION();
    @Override
    public RunningWildBitOp getOp() {
        return mOp;
    }

}
