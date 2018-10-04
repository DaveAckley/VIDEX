package com.putable.videx.std.vo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Hitmap;
import com.putable.videx.core.HitmapLocalSearcher;
import com.putable.videx.core.Int2D;
import com.putable.videx.core.SXRandom;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.XGraphics;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class ConnectorLine extends EventAwareVO {
    private int mInitCounter = 10;
    private VO mFrom = null;
    private VO mTo = null;
    private Int2D mFromPoint = null;
    private Int2D mToPoint = null;
    private boolean mArrowHeadTo= true;
    private boolean mArrowHeadFrom = false;
    private double mLineThickness = 4;
    private int mHCStepsPerUpdate = 50;
    private BasicStroke mLine = new BasicStroke((int) (mLineThickness+0.5));
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        if (mFromPoint == null || mToPoint == null)
            return;
        Point2D ofrom = this.mapPixelToVOCOrNull(mFromPoint.asDouble(), null);
        Point2D oto = this.mapPixelToVOCOrNull(mToPoint.asDouble(), null);
        double halfThickness = Math.max(1.0, mLineThickness / 2);
        int fx = (int) ofrom.getX();
        int fy = (int) ofrom.getY();
        int tx = (int) oto.getX();
        int ty = (int) oto.getY();
        double dx = 0;
        double dy = 0;
        if (fx < tx)
            dx = halfThickness;
        else if (fx > tx)
            dx = -halfThickness;
        if (fy < ty)
            dy = halfThickness;
        else if (fy > ty)
            dy = -halfThickness;
        if (dx == 0 && dy == 0)
            return;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(mLine);
        XGraphics.drawArrowLine(g2d,fx + dx, fy + dy, tx - dx, ty - dy, 5*mLineThickness, 2*mLineThickness, mArrowHeadTo, mArrowHeadFrom); 
        g2d.setStroke(oldStroke);
    }

    public VO getFrom() {
        return mFrom;
    }

    public void setFrom(VO mFrom) {
        this.mFrom = mFrom;
    }

    public VO getTo() {
        return mTo;
    }

    public void setTo(VO mTo) {
        this.mTo = mTo;
    }

    public boolean isDirected() {
        return mArrowHeadTo;
    }

    public void setArrowHeadTo(boolean head) {
        this.mArrowHeadTo = head;
    }

    public void setArrowHeadFrom(boolean head) {
        this.mArrowHeadFrom = head;
    }

    public double getLineThickness() {
        return mLineThickness;
    }

    public void setLineThickness(double mLineThickness) {
        this.mLineThickness = mLineThickness;
        this.mLine = new BasicStroke((int) (mLineThickness + 0.5));
    }

    public ConnectorLine() {
        super.setIsMouseAware(false); // ConnectorLines are not physical :(
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
    }

    public ConnectorLine(VO from, VO to, boolean arrowHeadTo, boolean arrowHeadFrom) {
        this();
        this.setFrom(from);
        this.setTo(to);
        this.setArrowHeadTo(arrowHeadTo);
        this.setArrowHeadFrom(arrowHeadFrom); 
    }

    private Point2D getPixelTarget(VO vo) {
        Point2D pt = vo.getPose().getObjectAnchor(null);
        return vo.mapVOCToPixel(pt, pt);
    }

    private boolean getPixelToward(Point2D fromPixelModifiable, Point2D toPixel) {
        int atx = (int) fromPixelModifiable.getX();
        int aty = (int) fromPixelModifiable.getY();
        int tox = (int) toPixel.getX();
        int toy = (int) toPixel.getY();

        int dist = Hitmap.distance(atx, aty, tox, toy);
        if (dist <= 2) {
            fromPixelModifiable.setLocation(toPixel);
            return true;
        }
        boolean moved = false;
        if (atx < tox) {
            ++atx;
            moved = true;
        } else if (atx > tox) {
            --atx;
            moved = true;
        }
        if (aty < toy) {
            ++aty;
            moved = true;
        } else if (aty > toy) {
            --aty;
            moved = true;
        }
        if (moved)
            fromPixelModifiable.setLocation(atx, aty);
        return moved;
    }
    private enum State { UNINIT, OFF, STUCK, MOVED };
    private State shcOnce(Hitmap hm, SXRandom random, VO target, Point2D goal, Point2D last) {
        // (1) Stochastically step towards goal pixel, starting from last, while on top
        // of target
        // (2) If not on top of target, step toward target.oax.pixel
        // (3) Update last with if move made
        // return true unless was on target but step (1) found no improvement
        if (!hm.isValid())
            return State.UNINIT; // Can't work now
        Point2D result = hm.pixelStochasticHillclimbStepOnVO(target, goal, last);
        if (result == null) { 
            System.out.println("LOST "+goal+" "+last);
            Point2D targetOAXPixel = target.mapVOCToPixel(target.getPose()
                    .getObjectAnchor(null), null);
            getPixelToward(last, targetOAXPixel);
            return State.OFF; // Better do only once step until we find our way..
        }
        if (((int) last.getX()) == ((int) result.getX())
                && ((int) last.getY()) == ((int) result.getY()))
            return State.STUCK;

        last.setLocation(result);
        return State.MOVED;
    }

    private boolean hcOnce(Hitmap hm, VO target, Point2D goal, Point2D last) {
        // (1) Step towards goal pixel, starting from last, while on top
        // of target
        // (2) If not on top of target, step toward target.oax.pixel
        // (3) Update last with if move made
        // return true unless was on target but step (1) found no improvement
        if (!hm.isValid())
            return true; // Can't work now
        Point2D result = hm.pixelHillclimbStepOnVO(target, goal, last);
        if (result == null) { 
            System.out.println("LOST "+goal+" "+last);
            Point2D targetOAXPixel = target.mapVOCToPixel(target.getPose()
                    .getObjectAnchor(null), null);
            getPixelToward(last, targetOAXPixel);
            return true; // Better do only once step until we find our way..
        }
        if (((int) last.getX()) == ((int) result.getX())
                && ((int) last.getY()) == ((int) result.getY()))
            return false;

        last.setLocation(result);
        return true;
    }

    private boolean hcOnceORIG(Hitmap hm, VO target, VO source, Point2D last) {
        // (1) Step towards source.oax.pixel, starting from last, while on top
        // of target
        // (2) If not on top of target, step toward target.oax.pixel
        // (3) Update last with if move made
        // return true unless was on target but step (1) found no improvement
        if (!hm.isValid())
            return true; // Can't work now
        Point2D sourceOAXPixel = source.mapVOCToPixel(source.getPose()
                .getObjectAnchor(null), null);
        Point2D result = hm
                .pixelHillclimbStepOnVO(target, sourceOAXPixel, last);
        if (result == null) { // Lost target, head for its anchor
            Point2D targetOAXPixel = target.mapVOCToPixel(target.getPose()
                    .getObjectAnchor(null), null);
            getPixelToward(last, targetOAXPixel);
            return true; // Better do only once step until we find our way..
        }
        if (((int) last.getX()) == ((int) result.getX())
                && ((int) last.getY()) == ((int) result.getY()))
            return false;

        last.setLocation(result);
        return true;
    }
    
    private class EndSearcher extends HitmapLocalSearcher {
        private VO mWalkingOnVO;
        private EndSearcher mOtherEnd = null;
        private Int2D mFrom;
        public EndSearcher(Hitmap hm, VO thisVO) {
            super(hm);
            mWalkingOnVO = thisVO;
        }
        public void setOtherEnd(EndSearcher es) {
            if (mOtherEnd != null) throw new IllegalStateException();
            mOtherEnd = es;
        }

        @Override
        public boolean source(Int2D from) {
            mFrom = from;
            return accept(mFrom);
        }

        @Override
        public Int2D vary(Int2D to) {
            if (to == null) to = new Int2D();
            to.set(mFrom);
            SXRandom random = getHitmap().getRandom();
            while (to.equals(mFrom)) {
                to.setX(mFrom.getX() + random.between(-2, 2));
                to.setY(mFrom.getY() + random.between(-2, 2));
            }
            return to;
        }

        @Override
        public boolean accept(Int2D pt) {
            return null != this.getHitmap().getChildAtPixel(mWalkingOnVO, pt.getX(), pt.getY());
        }

        @Override
        public double evaluate(Int2D pt) {
            return pt.euclideanDistanceSquared(this.mOtherEnd.mFrom);
        }
    }
    private void newStochasticHillclimb(Hitmap hm) {
        
        // Make two local searchers, that depend on each other's current points
        EndSearcher esfrom = new EndSearcher(hm,mFrom);
        EndSearcher esto = new EndSearcher(hm,mTo);
        esfrom.setOtherEnd(esto);
        esto.setOtherEnd(esfrom);
        esfrom.source(mFromPoint);
        esto.source(mToPoint);
        
        // Get working copies of endpoints
        Int2D ptFrom = new Int2D(mFromPoint);
        Int2D ptTo = new Int2D(mToPoint);

        // Check if they're both acceptable
        int check = 0;
        Int2D delta = ptFrom.subtract(ptTo, null);
        final int MAX_TRIES = 10; 
        while (++check <= MAX_TRIES) {
            if (!esfrom.accept(ptFrom)) {
                if (check < MAX_TRIES) // Extend current line 
                    ptFrom.set(ptFrom.add(delta.multiply(.05, null),null));
                else // F'it take the anchor
                    ptFrom.set(mFrom.mapVOCToPixel(mFrom.getPose().getObjectAnchor(null),null));
            }
            if (!esto.accept(ptTo)) {
                if (check < MAX_TRIES) // Extend current line 
                    ptTo.set(ptTo.add(delta.multiply(-.01, null),null));
                else // F'it take the anchor
                    ptTo.set(mTo.mapVOCToPixel(mTo.getPose().getObjectAnchor(null),null));
            }
        }
        if (!esfrom.accept(ptFrom) || !esto.accept(ptTo)) {
            System.out.println("CAN'T FIND TARGET(S) PUNTING");
            return;
        }

        // Stash recaptured targets
        if (!ptFrom.equals(mFromPoint)) mFromPoint.set(ptFrom);
        if (!ptTo.equals(mToPoint)) mToPoint.set(ptTo);

        // Capture our overall starting score
        double initScore = ptFrom.manhattanDistance(ptTo);
        
        // Make some alternating attempts to find better things
        for (int i = 0; i < this.mHCStepsPerUpdate; ++i) {
            ptFrom.set(hm.evaluateAcceptableVariants(esfrom, ptFrom, null, 10, 1));
            ptTo.set(hm.evaluateAcceptableVariants(esto, ptTo, null, 10, 1));
        }
        
        // If the results strictly beat our starting score, commit both ends.
        if (ptFrom.manhattanDistance(ptTo) < initScore) {
            mFromPoint.set(ptFrom);
            mToPoint.set(ptTo);
        }
    }

    /*
    private void doStochasticHillclimb(Hitmap hm, SXRandom random) {
        boolean tryFrom = true;
        boolean tryTo = true;
        State fromstat = State.UNINIT, tostat = State.UNINIT;
        // Final choices need to beat this to take effect
        int olddist = Hitmap.distance(mToPoint, mFromPoint);
        Point2D top = new Point2D.Float();
        top.setLocation(mToPoint);
        Point2D fromp = new Point2D.Float();
        fromp.setLocation(mFromPoint);
        for (int i = 0; i < mHCStepsPerUpdate; ++i) {
            if (tryFrom) {
                fromstat = shcOnce(hm, random, mTo, top, fromp); 
                //tryFrom = (fromstat != State.OFF);
            }
            if (tryTo) {
                tostat = shcOnce(hm, random, mFrom, fromp, top);
                //tryTo = (tostat != State.OFF);
            }
            if (!tryFrom && !tryTo)
                break;
        }
        int newdist = Hitmap.distance(top, fromp);
        if (newdist < olddist || fromstat == State.OFF || tostat == State.OFF) {
            mToPoint.setLocation(top);
            mFromPoint.setLocation(fromp);
        }
    }
    */
    
    @Override
    public boolean updateThisVO(Stage stage) {
        if (mInitCounter > 0) {
            --mInitCounter;
            return true;
        }
        Hitmap hm = stage.getHitmap();
        if (!hm.isValid()) return true; // 'handled'
        
        if (mFrom == null || mTo == null)
            return true; // Nothing to do
        if (mFromPoint == null)
            mFromPoint = new Int2D(getPixelTarget(mFrom));
        if (mToPoint == null)
            mToPoint = new Int2D(getPixelTarget(mTo));
//        doStochasticHillclimb(hm,stage.getRandom());
        newStochasticHillclimb(hm);
        return true;
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }
}
