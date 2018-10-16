package com.putable.videx.core;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Rider;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.vo.StageVO;

public abstract class StandardVO implements VO {
    /**
     * The object number associated with this VO, if it has been assigned
     * (NOT @OIO because it's special-cased.)
     */
    private int mOnum = -1;

    /**
     * Get the world from the nearest parental stage, if any
     * 
     * @return The world, if it is found, else null (if we run out of parents
     *         first, for whatever reason.)
     */
    public World getWorld() {
        VO p = this;
        while (p != null) {
            if (p instanceof StageVO) {
                StageVO s = (StageVO) p;
                return s.getWorld();
            }
            p = p.getParent();
        }
        return null;
    }

    // public abstract void writeOIOMembers(Writer w);

    /*
     * @Override public void writeOIO(Writer w, OIOAbleGlobalMap refs) throws
     * IOException { if (this.getOnum() <= 0) throw new IllegalStateException();
     * w.write(String.format("\n#%d:%s {\n", this.getOnum(),
     * this.getClass().getName())); this.writeOIOStandardMembers(w,refs);
     * this.writeOIOMembers(w,refs); w.write("}\n"); }
     */
    @Override
    public int getOnum() {
        return mOnum;
    }

    @Override
    public void setOnum(int onum) {
        if (onum <= 0)
            throw new IllegalArgumentException();
        if (mOnum != -1)
            throw new IllegalStateException();
        this.mOnum = onum;
    }

    /**
     * The actual current children of this VO
     */
    @OIO
    private LinkedList<VO> mVO = new LinkedList<VO>();

    private LinkedList<TriggerPoint> mTriggerPoints = new LinkedList<TriggerPoint>();

    private LinkedList<TriggerPoint> mPendingTriggerPoints = new LinkedList<TriggerPoint>();

    @Override
    public TriggerPoint addPendingTriggerPoint(double tx, double ty) {
        TriggerPoint tp = new TriggerPoint(tx, ty);
        this.mPendingTriggerPoints.add(tp);
        return tp;
    }

    @Override
    public Iterator<TriggerPoint> getTriggerPointIterator() {
        TriggerPoint tp;
        while ((tp = mPendingTriggerPoints.poll()) != null)
            mTriggerPoints.add(tp);
        return mTriggerPoints.iterator();
    }

    @Override
    public boolean handleOverlappedTriggerPoint(TriggerPoint tp, VO otherVO) {
        return false;
    }

    /**
     * The actual current Riders on this VO
     */
    @OIO
    private LinkedList<Rider> mRiders = new LinkedList<Rider>();

    public boolean addRider(Rider rider) {
        if (mRiders.contains(rider))
            return false;
        mRiders.add(rider);
        return true;
    }

    public Iterable<Rider> getRiders() {
        return mRiders;
    }

    /**
     * The parent of this VO, set when this VO is added to parent's mVO
     */
    @OIO(owned = false)
    private VO mParent = null;

    /**
     * Any newly-added children waiting to go onto mVO
     */
    @OIO
    private LinkedList<VO> mPending = new LinkedList<VO>();

    @OIO
    private boolean mKilled = false;
    @OIO
    private boolean mEnabled = true;

    @OIO
    private Pose mPose = new Pose();

    public <V extends VO> V findFirstInstance(Class<V> c) {
        for (VO vo : this) {
            if (c.isInstance(vo)) return c.cast(vo);
        }
        for (VO vo : this.pending()) {
            if (c.isInstance(vo)) return c.cast(vo);
        }
        return null;
    }
    /**
     * The AffineTransform from my VOC to my parent's VOC
     */
    private AffineTransform mPoseAT = new AffineTransform();

    /**
     * The AffineTransform from my VOC to Stage pixels, as of the last render
     */
    private AffineTransform mVOCToPixelAT = new AffineTransform();

    @OIO
    private Color mBackground = Color.DARK_GRAY;

    @OIO
    private Color mForeground = Color.LIGHT_GRAY;

    private int mHitmapColor = 0;

    @Override
    public int getHitmapColor() {
        // if (mHitmapColor == 0) throw new IllegalStateException();
        return mHitmapColor;
    }

    @Override
    public void setHitmapColor(int code) {
        if (mHitmapColor != 0)
            throw new IllegalStateException();
        code &= ~0xff000000;
        if (code == 0)
            throw new IllegalArgumentException();
        code |= 0xff000000;
        mHitmapColor = code;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public boolean setEnabled(boolean enable) {
        boolean old = mEnabled;
        mEnabled = enable;
        return old;
    }

    @Override
    public VO getParent() {
        return mParent;
    }

    @Override
    public void setParent(VO vo) {
        if (vo == null)
            throw new IllegalArgumentException("Argument must not be null");
        if (mParent != null)
            throw new IllegalStateException("Already have parent");
        mParent = vo;
    }

    @Override
    public void clearParent() {
        mParent = null;
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        return this.mVOCToPixelAT.transform(inVOC, outPixel);
    }

    @Override
    public Point2D mapPixelToVOCOrNull(Point2D inPixel, Point2D outVOC) {
        try {
            return this.mVOCToPixelAT.inverseTransform(inPixel, outVOC);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    @Override
    public AffineTransform getVOCToPixelTransform(AffineTransform at) {
        if (at == null)
            at = new AffineTransform();
        at.setTransform(mVOCToPixelAT);
        return at;
    }

    @Override
    public AffineTransform getVOCToParentVOCTransform(AffineTransform at) {
        if (at == null)
            at = new AffineTransform();
        at.setTransform(mPoseAT);
        return at;
    }

    public Point2D getParentVOC(Point2D ourvoc, Point2D parentvoc) {
        return mPoseAT.transform(ourvoc, parentvoc);
    }

    @Override
    public Color getBackground() {
        return mBackground;
    }

    public void setBackground(Color background) {
        this.mBackground = background;
    }

    @Override
    public Color getForeground() {
        return mForeground;
    }

    public void setForeground(Color foreground) {
        this.mForeground = foreground;
    }

    @Override
    public void addPendingChild(VO child) {
        if (child == null)
            throw new NullPointerException();
        mPending.add(child);
    }

    @Override
    public void forEach(Consumer<? super VO> arg0) {
        mVO.forEach(arg0);
    }

    public Iterable<VO> pending() {
        return mPending;
    }
    
    @Override
    public Iterator<VO> iterator() {
        return mVO.iterator();
    }

    @Override
    public Spliterator<VO> spliterator() {
        return mVO.spliterator();
    }

    @Override
    public Pose getPose() {
        return mPose;
    }

    @OIO
    private Integer mUnprocessedReorderRequest = null;
    
    @Override
    public int pollReorderRequest() {
        int ret = VO.VO_REORDER_NONE;
        if (mUnprocessedReorderRequest != null) {
            ret = mUnprocessedReorderRequest;
            mUnprocessedReorderRequest = null;
        }
        return ret;
    }

    public void requestTop() {
        mUnprocessedReorderRequest = VO.VO_REORDER_TOP;
    }
    
    @Override
    public void killVO() {
        // We are not recursing to kill kids here, because killing needs to be
        // safe anywhere. We just kill the top guy and let the kids be left
        // wiggling, but unreachable, below.
        // for (VO v : this) v.killVO();
        mKilled = true;
    }

    @Override
    public boolean isAlive() {
        return !mKilled;
    }

    @Override
    public void updateVO(Stage s) {
        // Nothing happens if we're dead
        if (!isAlive())
            return;

        // Incorporate any pending kids before doing update guts
        while (mPending.size() > 0) {
            VO kid = mPending.remove();
            kid.setParent(this);
            mVO.add(kid);
        }

        // Check for any reordering requests
        LinkedList<VO> tops = null;
        //LinkedList<VO> bottoms = null;
        for (Iterator<VO> iterator = mVO.iterator(); iterator.hasNext();) {
            VO vo = iterator.next();
            if (!vo.isAlive()) {
                // no way to clear parent link at present. vo.setParent(null);
                iterator.remove(); // Reap VOs known dead by this time
            } else {
                int req = vo.pollReorderRequest();
                if (req == VO.VO_REORDER_TOP) {
                    if (tops == null) tops = new LinkedList<VO>();
                    tops.add(vo);
                    iterator.remove();
                } else if (req != VO.VO_REORDER_NONE) {
                   System.err.println("UNKNOWN OR UNIMPLEMENTED REORDER REQ "+ req);
                }
            }                
        }
        if (tops != null) for (VO v : tops) mVO.add(v);
        //if (bottoms != null) for (VO v : bottoms) mVO.add(0, v);
        
        // If we're not enabled, no updating happens
        if (!isEnabled())
            return;

        // Process any attached Riders first
        for (Iterator<Rider> iterator = mRiders.iterator(); iterator
                .hasNext();) {
            Rider rider = iterator.next();
            int reaction = rider.react(this);
            switch (reaction) {
            case Rider.REACT_REAWAKEN:
                rider.awaken();
                /* FALL THROUGH */
            case Rider.REACT_REOBSERVE:
                rider.observe(this);
                /* FALL THROUGH */
            case Rider.REACT_CONTINUE:
            case Rider.REACT_REASSERT:
                rider.act();
                break;
            case Rider.REACT_DIE:
            case Rider.REACT_REMOVE:
                iterator.remove();
                break;
            case Rider.REACT_KILL:
                this.killVO();
                return;
            default:
                throw new IllegalStateException();
            }
        }

        // Now do any type-specific transformation
        if (!this.updateThisVO(s))
            return;

        // Finally, do the kids
        for (Iterator<VO> iterator = mVO.iterator(); iterator.hasNext();) {
            VO vo = iterator.next();
            if (!vo.isAlive()) {
                // no way to clear parent link at present. vo.setParent(null);
                iterator.remove(); // Reap VOs known dead by this time
            } else
                vo.updateVO(s);
        }
    }

    public void updatePoseTransform() {
        mPose.initAffineFrom(mPoseAT); // Our transform relative to our parents
        VO ourParent = getParent();
        if (ourParent == null)
            mVOCToPixelAT.setToIdentity();
        else
            mVOCToPixelAT.setTransform(ourParent.getVOCToPixelTransform(null));
        mVOCToPixelAT.concatenate(mPoseAT);
    }

    @Override
    public void computeThisTransformVO(World world) {
        updatePoseTransform();
    }

    @Override
    public void computeTransformVO(World w) {

        // Nothing happens if we're dead or disabled
        if (!isAlive() || !isEnabled())
            return;

        this.computeThisTransformVO(w);

        for (VO vo : this)
            vo.computeTransformVO(w);
    }

    @Override
    public void drawVO(VOGraphics2D v2d) {

        if (!isEnabled() || !isAlive())
            return;

        v2d.renderVO(this);

        for (VO vo : mVO)
            vo.drawVO(v2d);
    }

    /**
     * Get FontMetrics for use while drawing this VO. This code assumes the
     * current transform is derived from this.getPose() (which it will be under
     * normal circumstances in {@link #drawThisVO(VOGraphics2D)}) plus an
     * addition rotation of {@code extraradians}. Leaves {@code g2d} unaffected.
     * 
     * @param g2d
     *            the Graphics2D to derive the FontMetrics from
     * 
     * @param extraradians
     *            additional radians of rotation to add to that implied by the
     *            current Pose, before deriving the FontMetrics
     * 
     * @return a new FontMetrics with hopefully correct getAscent() and
     *         getDescent() values.
     * 
     *         <pre>
     * 
     * NOTES:
     * 
     * Well, so this is just great.
     * 
     * [88: Fri Sep 7 04:51:01 2018 Well, working on WildBitVector now, and was
     * seeing weird behavior where my 0s and 1s were aligned when I drew the
     * thing, but their vertical position drifted when I rotated the whole
     * vector.
     * 
     * It was as if FontMetrics.getAscent() and .getDescent() somehow cared what
     * the overall rotation of the text was. Which makes no sense?
     * 
     * Yes. It makes no sense. Because it's a fucking Java bug.
     * 
     * It's an instance of https://bugs.openjdk.java.net/browse/JDK-8139178 -
     * 
     * .. Wrong fontMetrics when printing in Landscape (OpenJDK) .. A
     * DESCRIPTION OF THE PROBLEM : When printing in landscape, getting
     * FontMetrics returns wrong font parameters - ascent=0, descent=0, height=0
     * 
     * Thanks. Thanks Guys. And, the bug is tagged 'OpenJDK-only' so WTFnose
     * when if ever it'll be fixed.
     * 
     *         </pre>
     */
    public FontMetrics getUnscrewedFontMetrics(Graphics2D g2d,
            double extraradians) {
        AffineTransform at = g2d.getTransform();
        double rot = this.getNetRotationFromHereToTop() + extraradians;
        g2d.rotate(-rot); // Produce an unrotated version of our transform
        FontMetrics fm = g2d.getFontMetrics(); // Get font metrics from that.
                                               // Thanks. Thanks.
        g2d.setTransform(at); // Put back the real transform
        return fm;
    }

    public double getNetRotationFromHereToTop() {
        VO up = this;
        double rot = 0;
        while (up != null) {
            rot += up.getPose().getR();
            up = up.getParent();
        }
        return rot;
    }
}
