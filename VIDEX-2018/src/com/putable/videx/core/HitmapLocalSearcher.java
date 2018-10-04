package com.putable.videx.core;

public abstract class HitmapLocalSearcher {

    private final Hitmap mHitmap;

    public Hitmap getHitmap() {
        return mHitmap;
    }

    /**
     * @param hitmap
     */
    public HitmapLocalSearcher(Hitmap hitmap) {
        mHitmap = hitmap;
    }

    /**
     * Specify where variations shall be sought
     * 
     * @param from
     *            the place to consider
     * @return false if that's not an acceptable beginning
     */
    public abstract boolean source(Int2D from);

    /**
     * Produce a variation on the previously specified {@link #source(Int2D)}.
     * 
     * @param variant
     *            destination to store into; if null allocate new Int2D
     * @return to, or null if the generator is exhausted
     */
    public abstract Int2D vary(Int2D variant);

    /**
     * Determine if pt is an acceptable place to evaluate
     * 
     * @param pt
     * @return false if pt is unacceptable and therefore cannot be
     *         {@link #evaluate(Int2D)}; true otherwise
     */
    public abstract boolean accept(Int2D pt);

    /**
     * Evaluate an acceptable point at return a score. Behavior is undefined if
     * pt was not approved by {@link #accept(Int2D)}
     * 
     * @param pt
     *            point to evaluate
     * @return its score
     */
    public abstract double evaluate(Int2D pt);
}