package com.putable.videx.core;

import com.putable.xrandom.XRandom;

public class SXRandom {
    private final XRandom mXrandom = new XRandom();

    public int create(int n) {
        return mXrandom.nextInt(n);
    }

    public int createBits(int bitcount) {
        if (bitcount <= 0) return 0;
        if (bitcount > 32) throw new IllegalArgumentException();
        if (bitcount == 32) return mXrandom.nextInt();
        return create(1<<bitcount);
    }

    public int zcreate(int n) {
        if (n <= 0)
            return 0;
        return create(n);
    }

    public int between(int lo, int hi) {
        return mXrandom.nextRange(lo, hi);
    }

    public float nextFloat() { return mXrandom.nextFloat(); }
    public double nextDouble() { return mXrandom.nextDouble(); }
    
    public double around(double center, double radius) {
        double dist = mXrandom.nextDouble()*radius;
        if (mXrandom.nextBoolean()) dist = -dist;
        return center + dist;
    }

    public boolean probabilityOf(double probability) {
        return mXrandom.nextProbability(probability);
    }
    public boolean oddsOf(int n, int outOf) {
        return mXrandom.nextProbability(n, outOf);
    }

    public boolean oneIn(int n) { return oddsOf(1,n); }
    
}
