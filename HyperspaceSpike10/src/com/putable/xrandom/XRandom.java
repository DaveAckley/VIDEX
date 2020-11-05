package com.putable.xrandom;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

import java.util.Map;
import java.util.Random;

/**
 * A small extension of java.util.Random, providing a couple of potentially
 * handy methods Random is missing.
 * 
 * @author ackley, terran
 * @version 1.4 - 20071124 Now extending Sean Luke's 'MersenneTwisterFast'
 *          implementation, so the resulting random numbers don't utterly blow.
 */

public strictfp class XRandom extends MersenneTwisterFast {
    public static final long serialVersionUID = 1;

    private static Random seedGenerator = new Random();

    private static long prepareSeed(long seed) {
        return (seed == 0) ? getRandomSeed() : seed;
    }

    /**
     * Obtain a randomly-selected non-zero long value suitable for use as an
     * XRandom random number seed.
     * 
     * @return a random non-zero long value.
     */
    public synchronized static long getRandomSeed() {
        long nextSeed;
        do {
            nextSeed = seedGenerator.nextLong() ^ System.currentTimeMillis();
            nextSeed >>>= 1; // Ensure positive
        } while (nextSeed == 0); // Sure. Right. Gonna happen.
        return nextSeed;
    }

    private long theSeed;

    /**
     * Create an XRandom initialized with a randomly-chosen seed, so results
     * will tend to differ from run to run even if nothing else changes.
     */
    public XRandom() {
        super();
        this.setSeedLong(getRandomSeed());
    }

    /**
     * Create an XRandom initialized with the specified seed, so results will
     * remain identical from run to run -- so long as the same seed is used, and
     * all other random objects and methods (such as Math.random()) are
     * scrupulously avoided.
     * 
     * @param seed
     *            the seed to initialize the generator with. If seed == 0, pick
     *            a random non-zero seed value, otherwise use the supplied value
     *            as-is.
     */
    public XRandom(long seed) {
        super();
        this.setSeedLong(seed);
    }

    /**
     * This method should be used instead of setSeed(long), because that method
     * only uses the bottom 32 bits of the long for seeding the generator. This
     * method <i>would</i> have simply overridden setSeed(long) to fix it, but
     * it <i>can't</i> because the broken setSeed(long) method is called from
     * within MersenneTwisterFast and its broken behavior is required.
     * 
     * @param seed
     */
    public void setSeedLong(long seed) {
        this.theSeed = prepareSeed(seed);
        // Let's not just use 32 bits of a whole long seed, I mean please.
        int[] seedArray = { (int) (theSeed & 0xffffffff),
                (int) (theSeed >>> 32) };
        super.setSeed(seedArray);
    }

    /**
     * Get the seed.
     * 
     * @return the actual seed value (last) used to initialize this PRNG. NOTE:
     *         this method will never return 0. If the generator was seeded (via
     *         ctor or setSeedLong) with seed == 0, this method will return the
     *         actual randomly-drawn non-zero seed that was used instead.
     */
    public long getSeed() {
        return theSeed;
    }

    /**
     * Draw a sample from an exponentially distributed random variable with the
     * given mean value.
     * 
     * @param mean
     *            the specified average value of the produced numbers
     * @return a sample drawn from exponentially distributed random variable
     *         with average value 'mean'
     * @throws IllegalArgumentException
     *             if the mean is equal to zero
     */
    public double nextExponential(double mean) {
        if (mean == 0.0)
            throw new IllegalArgumentException("Zero mean illegal");
        double lambda = 1.0 / mean;
        double u;
        do {
            u = nextDouble();
        } while (u == 0.0);
        return (-1.0 / lambda) * log(u);
    }

    /**
     * Draw a sample from a geometric distribution with parameter <i>p</i>. The
     * mean of the resulting geometric distribution will be <i>1/p</i>.
     * 
     * @param p
     *            Parameter of the geometric distribution.
     * @return Sample drawn from geometric distribution with parameter <i>p</i>.
     * @throws IllegalArgumentException
     *             if <i>p</i> is not a probability (i.e., if
     *             {@code p<0.0 || p>1.0}).
     */
    public int nextGeometric(double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("Geometric parameter, p, "
                    + "must be in range [0.0,1.0]");
        }
        double u = nextDouble();
        return (int) ceil(log(1 - u) / log(1 - p));
    }

    /**
     * Return an integer drawn uniformly at random from minInclusive to
     * maxInclusive, including both endpoints.
     * 
     * @param minInclusive
     *            the least value that can be returned
     * @param maxInclusive
     *            the greatest value that can be returned
     * @return the chosen number
     */
    public int nextRange(int minInclusive, int maxInclusive) {
        int range = maxInclusive - minInclusive + 1;
        return this.nextInt(range) + minInclusive;
    }

    /**
     * Return true at random the specified fraction of the time. Setting
     * probability to 0.5 makes this method act the same as nextBoolean().
     * Setting it to 0.0 makes this method always return false, setting it to
     * 1.0 makes it always return true.
     * 
     * @param probability
     *            the chance of returning true
     * @return true with the given probability, false with 1-probability
     * @throws IllegalArgumentException
     *             if probability is less than 0.0 or greater than 1.0
     * 
     */
    public boolean nextProbability(double probability) {
        if (probability < 0 || probability > 1.0)
            throw new IllegalArgumentException("Bad probability");
        return nextDouble() <= probability;
    }

    /**
     * Return true at random 'thisChance' out of 'thisRange' fraction of the
     * time.  If thisChance == 0, always returns false; if thisChance == thisRange, 
     * always returns true
     * 
     * @param thisChance
     *            the numerator in the probability
     * @param thisRange
     *            the denominator in the probability
     * @return true with the given probability, false with 1-probability
     * @throws IllegalArgumentException
     *             if thisChance is less than 0 or
     *             thisRange is less than 1 or thisChance &gt; thisRange
     * 
     */
    public boolean nextProbability(int thisChance, int thisRange) {
        if (thisChance < 0 || thisRange < 1 || thisChance > thisRange)
            throw new IllegalArgumentException("Bad chance=" + thisChance
                    + " or range=" + thisRange);
        return nextInt(thisRange) < thisChance;
    }

    /**
     * Draws an index from an arbitrary (discrete) CDF. This selects one entry
     * out of an array containing cumulative probabilities, and returns the
     * index of the selected entry.
     * <p>
     * The contents of the array are assumed to be CDF values, sorted in
     * increasing order. That is, it is required that:
     * 
     * <pre>
     *           cdf[j]&gt;=cdf[i]  for all j&gt;i
     *           cdf[cdf.length-1]==1.0
     * </pre>
     * 
     * If violations of these conditions are detected, an
     * {@link IllegalStateException} is generated.
     * 
     * @param cdf
     *            Array containing conditional distribution function data
     * @return index of selected outcome
     * @throws IllegalStateException
     *             if {@code cdf} appears to be non-normalized.
     */
    public int drawFromCDF(double[] cdf) {
        double v = nextDouble();
        for (int result = 0; result < cdf.length; ++result) {
            if (v <= cdf[result]) {
                return result;
            }
        }
        throw new IllegalStateException("CDF not normalized.");
    }

    /**
     * Draws a value from a discrete probability mass function (PMF) and returns
     * the corresponding outcome. The PMF is required to be represented as a
     * {@link Map} from the outcome space onto probabilities. It is required
     * that:
     * <ul>
     * <li> All probability values in the map are {@code >=0.0}
     * <li> The sum of all probabilities in the map is {@code 1.0}.
     * </ul>
     * <p>
     * If a violation of these conditions is detected, this throws a
     * {@link IllegalStateException}.
     * 
     * @param <T>
     *            Type of the outcome space
     * @param pmf
     *            {@link Map} from outcome space ({@code T}) onto
     *            probabilities.
     * @return Element drawn from the outcome space according to the pmf.
     * @throws IllegalStateException
     *             if the pmf is not properly normalized.
     */
    public <T> T drawFromPMF(Map<T, Double> pmf) {
        double cumMass = 0.0;
        double v = nextDouble();
        for (T val : pmf.keySet()) {
            cumMass += pmf.get(val);
            if (v <= cumMass) {
                return val;
            }
        }
        throw new IllegalStateException("PDF not normalized.");
    }

    /**
     * Draws a value from a discrete probability mass function (PMF) and returns
     * the corresponding outcome. The PMF is required to be represented as an
     * array of probabilities, and the returned value is the index of the
     * selected element. It is required that:
     * <ul>
     * <li> All probability values in the array are {@code >=0.0}
     * <li> The sum of all probabilities in the array is {@code 1.0}.
     * </ul>
     * <p>
     * If a violation of these conditions is detected, this throws a
     * {@link IllegalStateException}.
     * 
     * @param pmf
     *            Array of probability values for each index
     * @return Index drawn according to the pmf.
     * @throws IllegalStateException
     *             if the pmf is not properly normalized.
     */
    public int drawFromPMF(double[] pmf) {
        double cumMass = 0.0;
        double v = nextDouble();
        for (int idx = 0; idx < pmf.length; ++idx) {
            cumMass += pmf[idx];
            if (v <= cumMass) {
                return idx;
            }
        }
        throw new IllegalStateException("PDF not normalized.");
    }

}
