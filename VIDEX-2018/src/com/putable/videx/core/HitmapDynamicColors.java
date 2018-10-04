package com.putable.videx.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import com.putable.videx.interfaces.VO;

public class HitmapDynamicColors {

    /**
     * Search for improvements around point 'from'.
     * 
     * @param ls
     * @param from
     *            starting point
     * @param maxGenerate
     *            maximum number of variations to consider while searching for
     *            one acceptable point
     * @param maxEvaluate
     *            maximum number of acceptable variations to evaluate
     * @return null if from is not an {@link #accept(Int2D)}-able point,
     *         otherwise return the best point found (may be a copy of from if
     *         no improvement).
     */
    public Int2D evaluateAcceptableVariants(HitmapLocalSearcher ls, Int2D from, Int2D to,
            int maxGenerate, int maxEvaluate) {
        if (!ls.accept(from))
            return null;
        if (to == null) to = new Int2D();
        to.set(from);

        double bestScore = 0; // Can't do ls.evaluate(from) here since no source() call yet
        int hits = 0;
        int evaluations = 0;
        while (evaluations++ < maxEvaluate) {
            Int2D variant = this.seekAcceptableVariant(ls, from, null, maxGenerate);
            if (variant == null) break;
            if (hits == 0) {
                hits = 1;
                bestScore = ls.evaluate(from);  
            }
            double vscore = ls.evaluate(variant);
            if (vscore < bestScore) {
                bestScore = vscore;
                to.set(variant);
                hits = 1;
            } else if (vscore == bestScore && HitmapDynamicColors.this.getRandom().oneIn(++hits)) {
                to.set(variant);
            }
        }
        return to;
    }

    public Int2D seekAcceptableVariant(HitmapLocalSearcher ls, Int2D from, Int2D to,
            int maxGenerate) {
        if (!ls.source(from))
            throw new IllegalArgumentException();
        if (to == null)
            to = new Int2D();
        for (int g = 0; g < maxGenerate; ++g) {
            ls.vary(to);
            if (ls.accept(to))
                return to;
        }
        return null;
    }

    private final SXRandom mRandom;

    public SXRandom getRandom() {
        return mRandom;
    }

    private BufferedImage mHitmapImage;
    private int mHitmapWidth;
    private int mHitmapHeight;
    private Graphics2D mHitmapGraphics;
    private HashMap<Integer, VO> mIndexToVO = new HashMap<Integer, VO>();
    private HashMap<VO, Integer> mVOToIndex = new HashMap<VO, Integer>();
    private final int MIN_INDEX = 1000000;
    private final int MAX_INDEX = 11000000;
    private int mMinIndex = MAX_INDEX;
    private int mMaxIndex = MAX_INDEX;
    private boolean mHitmapValid;

    public boolean isValid() {
        return mHitmapValid;
    }

    private int getHitIndexForVO(VO forVO) {
        Integer idx = mVOToIndex.get(forVO);
        if (idx != null)
            return idx;
        idx = mMaxIndex++;
        mVOToIndex.put(forVO, idx);
        mIndexToVO.put(idx, forVO);
        return idx;
    }

    private VO getVOForHitIndex(int idx) {
        if (idx < mMinIndex || idx >= mMaxIndex)
            return null;
        return mIndexToVO.get(idx);
    }

    private void resetHitIndex() {
        mVOToIndex.clear();
        mIndexToVO.clear();
        if (mMaxIndex < MAX_INDEX)
            mMinIndex = mMaxIndex;
        else { // After a billion VOs drawn, reset
            mMinIndex = MIN_INDEX;
            mMaxIndex = MIN_INDEX;
            mHitmapGraphics.setColor(Color.white);
            mHitmapGraphics.fillRect(0, 0, mHitmapWidth, mHitmapHeight);
        }
    }

    public Graphics2D getGraphics2D() {
        return mHitmapGraphics;
    }

    /**
     * Return the deepest descendant child of parent that drew to pixel
     * (pixy,pixy), if any, or else null.
     */
    public VO getChildAtPixel(VO parent, int pixx, int pixy) {
        VO child = getVOForPixel(pixx, pixy);
        VO vo = child;
        while (vo != null) {
            if (vo == parent)
                return child;
            vo = vo.getParent();
        }
        return null;
    }

    public static int distance(Point2D pt1, Point2D pt2) {
        return distance((int) pt1.getX(), (int) pt1.getY(), (int) pt2.getX(),
                (int) pt2.getY());
    }

    public static int distance(int fx, int fy, int tx, int ty) {
        return (fx - tx) * (fx - tx) + (fy - ty) * (fy - ty);
    }

    /**
     * Find a pixel that's neighboring fromPixel but closer to towardPixel,
     * while staying within the area drawn by vo or its children.
     * 
     * @param vo
     * @param towardPixel
     *            where to step toward
     * @param fromPixel
     *            where to take a step from. If null, start from vo.OA.
     * @return null if fromPixel is not located on vo, otherwise return a
     *         Point2D. If the returned point is equal to fromPixel (or to
     *         vo.OA, if fromPixel was passed in as null), there is no hillclimb
     *         step that moves closer to towardPixel, otherwise the returned
     *         Point2D is the coordinates of such a pixel.
     */
    public Point2D pixelHillclimbStepOnVO(VO vo, Point2D towardPixel,
            Point2D fromPixel) {
        if (fromPixel == null) {
            Point2D targ = vo.getPose().getObjectAnchor(null);
            fromPixel = vo.mapVOCToPixel(targ, null);
        }
        int atx = (int) fromPixel.getX();
        int aty = (int) fromPixel.getY();
        int tox = (int) towardPixel.getX();
        int toy = (int) towardPixel.getY();

        VO steppingOn = getChildAtPixel(vo, atx, aty);

        // If not even starting on vo, caller will need another plan
        if (steppingOn == null)
            return null;

        // Otherwise we're grounded. Establish current distance
        int dist = distance(atx, aty, tox, toy);

        int hits = 0;
        int pickx = 0, picky = 0; // Avoid uninitted var warning

        for (int dx = -1; dx <= 1; ++dx)
            for (int dy = -1; dy <= 1; ++dy) {
                if (dx == 0 && dy == 0)
                    continue;

                int nx = atx + dx;
                int ny = aty + dy;
                if (getChildAtPixel(vo, nx, ny) == null)
                    continue;

                int ndist = distance(nx, ny, tox, toy);
                if (ndist < dist) {
                    dist = ndist;
                    pickx = nx;
                    picky = ny;
                    hits = 1;
                } else if (ndist == dist) {
                    if (mRandom.oneIn(++hits)) {
                        pickx = nx;
                        picky = ny;
                    }
                }
            }
        if (hits > 0)
            return new Point2D.Float(pickx, picky);
        return null;
    }

    /**
     * Try to find a pixel that's neighboring fromPixel but might be closer to
     * towardPixel, while staying within the area drawn by vo or its children.
     * 
     * @param vo
     * @param towardPixel
     *            where to step toward
     * @param fromPixel
     *            where to take a step from. If null, start from vo.OA.
     * @return null if fromPixel is not located on vo, otherwise return a
     *         Point2D. If the returned point is equal to fromPixel (or to
     *         vo.OA, if fromPixel was passed in as null), there is no hillclimb
     *         step that moves closer to towardPixel, otherwise the returned
     *         Point2D is the coordinates of such a pixel.
     */
    public Point2D pixelStochasticHillclimbStepOnVO(VO vo, Point2D towardPixel,
            Point2D fromPixel) {
        if (fromPixel == null) {
            Point2D targ = vo.getPose().getObjectAnchor(null);
            fromPixel = vo.mapVOCToPixel(targ, null);
        }
        int atx = (int) fromPixel.getX();
        int aty = (int) fromPixel.getY();
        int tox = (int) towardPixel.getX();
        int toy = (int) towardPixel.getY();

        VO steppingOn = getChildAtPixel(vo, atx, aty);

        // If not even starting on vo, caller will need another plan
        if (steppingOn == null)
            return null;

        // Otherwise we're grounded. Establish current distance
        int dist = distance(atx, aty, tox, toy);

        int hits = 0;
        int pickx = 0, picky = 0; // Avoid uninitted var warning
        int dx = 0, dy = 0;

        while (dx == 0 && dy == 0) {
            dx = mRandom.between(-2, 2);
            dy = mRandom.between(-2, 2);
        }

        int nx = atx + dx;
        int ny = aty + dy;
        if (getChildAtPixel(vo, nx, ny) == null)
            return fromPixel; // Don't step off target..

        int ndist = distance(nx, ny, tox, toy);
        int delta = ndist - dist;
        final double TEMPERATURE = 10.0;
        double prob = 1 / (1 + Math.exp(delta) / TEMPERATURE);
        if (mRandom.probabilityOf(prob))
            return new Point2D.Float(nx, ny);
        return fromPixel;
    }

    public VO getVOForPixel(int x, int y) {
        if (!mHitmapValid)
            throw new IllegalStateException("Thread "
                    + SwingUtilities.isEventDispatchThread());
        if (x < 0 || y < 0 || x >= mHitmapWidth || y >= mHitmapHeight)
            return null;
        int coloridx = mHitmapImage.getRGB(x, y) & 0x00ffffff; // Dump the alpha
        return getVOForHitIndex(coloridx);
    }

    public void startDrawing() {
        this.resetHitIndex();
        mHitmapValid = false;
    }

    public void finishDrawing() {
        mHitmapValid = true;
    }

    public void resizeHitmap(int width, int height) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Dimensions must be positive");
        mHitmapWidth = width;
        mHitmapHeight = height;
        mHitmapImage = new BufferedImage(mHitmapWidth, mHitmapHeight,
                BufferedImage.TYPE_INT_ARGB);
        mHitmapGraphics = mHitmapImage.createGraphics();
        mHitmapValid = false;
        mMinIndex = MAX_INDEX; // Force full reset on new BI
        resetHitIndex();
    }

    public void drawHitmap(Graphics2D toG2D) {
        toG2D.drawImage(mHitmapImage, 0, 0, mHitmapWidth, mHitmapHeight, null);
    }

    public void initGraphicsForVO(VO vo) {
        Color index = new Color(this.getHitIndexForVO(vo), false);
        mHitmapGraphics.setColor(index);
        mHitmapGraphics.setBackground(index);
    }

    public HitmapDynamicColors(SXRandom random) {
        mRandom = random;
    }

}
