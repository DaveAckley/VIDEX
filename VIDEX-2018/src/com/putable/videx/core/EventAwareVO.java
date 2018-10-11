package com.putable.videx.core;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;

/**
 * A base class for clickable, draggable, wheel-zoomable objects. It implements
 * the following UI behaviors:
 * 
 * - Ctrl-drag-1 for VO dragging
 * 
 * - Ctrl-wheel for VO zooming
 * 
 * @author ackley
 * 
 */
public abstract class EventAwareVO extends StandardVO {
    @OIO
    private boolean mMouseAwarenessEnabled = true;
    @OIO
    private boolean mIsMouseTarget = false;
    @OIO
    private boolean mIsDraggable = true;
    @OIO
    private Point2D mDragStartVOC = new Point2D.Float(); // Initial mouse
                                                         // press location in
                                                         // Stage pixels

    @OIO
    private boolean mFocusAwarenessEnabled = false;

    private List<IOEventFilterMouse> mListOfEventFilterMouse = new LinkedList<IOEventFilterMouse>();

    public boolean addFilter(IOEventFilterMouse ioefm) {
        if (mListOfEventFilterMouse.contains(ioefm))
            return false;
        mListOfEventFilterMouse.add(ioefm);
        return true;
    }

    public void setIsMouseAware(boolean isAware) {
        this.mMouseAwarenessEnabled = isAware;
    }

    public void setIsFocusAware(boolean isFocusAware) {
        mFocusAwarenessEnabled = isFocusAware;
    }

    @Override
    public boolean isFocusAware() {
        return mFocusAwarenessEnabled;
    }

    @Override
    public boolean isMouseAware() {
        return super.isAlive() && super.isEnabled() && mMouseAwarenessEnabled;
    }

    @Override
    public void mouseEntered(Point2D at) {
        mIsMouseTarget = true;
    }

    @Override
    public void mouseExited() {
        mIsMouseTarget = false;
    }

    private Point2D mapPixelToVOCOrDie(Point2D inPixel) {
        Point2D ret = mapPixelToVOCOrNull(inPixel, null);
        if (ret == null) {
            System.exit(99); // Buh-eye
        }
        return ret;
    }

    @Override
    public void dragAtStage(Point2D at, int state) {
        if (state > 0) {
            mDragStartVOC = this.mapPixelToVOCOrDie(at);
            // this.moveAnchorTo(mDragStartVOC);
            // this.updatePoseTransform();
        } else if (state < 0) {
            mDragStartVOC = null;
        } else {
            if (mDragStartVOC == null) {
                System.err.println("EAVO dragTO with no start pixel " + at);
                return;
            }
            Point2D dragNowVOC = this.mapPixelToVOCOrDie(at);
            Point2D delta = new Point2D.Double(
                    dragNowVOC.getX() - mDragStartVOC.getX()
                            + this.getPose().getOAX(),
                    dragNowVOC.getY() - mDragStartVOC.getY()
                            + this.getPose().getOAY());
            AffineTransform toPar = this.getVOCToParentVOCTransform(null);
            Point2D deltaParentVOC = null;
            deltaParentVOC = toPar.transform(delta, null);
            this.getPose().setPAX((float) (deltaParentVOC.getX()));
            this.getPose().setPAY((float) (deltaParentVOC.getY()));
            this.updatePoseTransform();
            System.out.printf("YONG (%d,%d)\n", (int) deltaParentVOC.getX(),
                    (int) deltaParentVOC.getY());
        }
    }

    @Override
    public boolean rotateAround(Point2D at, double amount) {
        Point2D rotatePointVOC = this.mapPixelToVOCOrDie(at);
        this.moveAnchorTo(rotatePointVOC);

        double rot = this.getPose().getR();
        final double ROT_SCALE = Math.PI / 32.0;
        rot += ROT_SCALE * amount;
        this.getPose().setR((float) rot);
        this.updatePoseTransform();
        // this.getPose().setRXRY(oldrxy);
        System.out.printf("RANG %f -> %f\n", amount, this.getPose().getR());
        return true;
    }

    /**
     * Move the anchor point to newOA, in the object and in its parent, so that
     * the object doesn't move as a result of the changed anchor point. NOTE:
     * updatePoseTransform() must be called after this operation
     * 
     * @param newOA
     */
    public void moveAnchorTo(Point2D newOA) {
        Point2D phit = this.getParentVOC(newOA, null);
        Pose pose = this.getPose();
        System.out.println("MOVEANCHOR OA: " + newOA + ", PA: " + phit
                + " in pose: " + pose);
        pose.setPAX((float) phit.getX());
        pose.setPAY((float) phit.getY());
        pose.setOAX((float) newOA.getX());
        pose.setOAY((float) newOA.getY());
    }

    @Override
    public boolean zoomAround(Point2D at, double amount) {
        if (at == null)
            return false; // WHA?

        Point2D zoomPointVOC = this.mapPixelToVOCOrDie(at);
        this.moveAnchorTo(zoomPointVOC);
        double scalex = this.getPose().getSX();
        double scaley = this.getPose().getSY();
        final double TWELVTH_ROOT_OF_TWO = Math.pow(2, 1.0 / 12.0);
        double multiplier = Math.pow(TWELVTH_ROOT_OF_TWO, amount);
        scalex *= multiplier;
        scaley *= multiplier;
        this.getPose().setSX((float) scalex);
        this.getPose().setSY((float) scaley);
        this.updatePoseTransform();
//        System.out.printf("ZANG %f -> *%f,%f\n", amount, this.getPose().getSX(),
//                this.getPose().getSY());
        return true;
    }

    @Override
    public boolean handleMouseEvent(MouseEventInfo mei) {
        MouseEvent me = mei.getMouseEvent();

        for (IOEventFilterMouse ioefm : mListOfEventFilterMouse) {
            if (ioefm.matches(mei))
                return true;
        }
        return false;
    }

    /**
     * Perhaps handle keyboard event described by kei
     * 
     * @param kei
     *            The in-progress keyboard event
     * @return true iff the event has now been handled and propagation should
     *         stop, false otherwise
     */
    public abstract boolean handleKeyboardEventHere(KeyboardEventInfo kei);

    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        for (VO kid : this) {
            if (kid.handleKeyboardEvent(kei))
                return true;
        }
        return isFocusAware() && handleKeyboardEventHere(kei);
    }

    @Override
    public boolean handleSpecialEvent(SpecialEventInfo mei) {
        throw new UnsupportedOperationException("XXX");
        // return false;
    }

}
