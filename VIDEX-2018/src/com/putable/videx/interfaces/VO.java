package com.putable.videx.interfaces;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;

import com.putable.videx.core.Pose;
import com.putable.videx.core.TriggerPoint;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;

/**
 * Interface of all Visualization Objects in VIDEX.  Iterable to get its kids.
 * 
 * @author ackley
 * 
 */
public interface VO extends Iterable<VO>, OIOAble {
    
    @Override
    int getOnum();
    
    /**
     * Current position, rotation, and scale of this VO relative to its parent
     * 
     * @return current pose
     */
    Pose getPose();

    /**
     * Current foreground color for this VO
     * 
     * @return the color
     */
    Color getForeground();

    /**
     * Current background color for this VO
     * 
     * @return the color
     */
    Color getBackground();

    /**
     * Get the transformation that maps position in this VO to position in its
     * parent VO
     * 
     * @param at
     *            if non-null, update this transformation, or else allocate a
     *            new one
     * @return the existing or new AffineTransform
     */
    AffineTransform getVOCToParentVOCTransform(AffineTransform at);

    /**
     * Get the transformation that maps position in this VO to pixels in stage
     * coordinates
     * 
     * @param at
     *            if non-null, update this transformation, or else allocate a
     *            new one
     * @return the existing or new AffineTransform
     */
    AffineTransform getVOCToPixelTransform(AffineTransform at);

    /**
     * Mark this VO as dead and eligible for reaping. WARNING: This VO may be
     * removed later in the current update or during the next update.
     */
    void killVO();

    /**
     * 
     * @return true unless this object is scheduled for reaping
     */
    boolean isAlive();

    /**
     * 
     * @return true if this VO is currently enabled
     */
    boolean isEnabled();

    /**
     * Enable or disable this VO. Disabled VOs do not update or render -- they
     * act (almost) entirely as if they are dead, except they are not reaped and
     * can be reenabled later.
     * 
     * @param enable
     *            whether to enable or disable this VO
     * @return the old enabled status
     */
    boolean setEnabled(boolean enable);

    /**
     * Update this VO only
     * 
     * @return true if its kids should be updated
     */
    boolean updateThisVO(Stage stage);

    /**
     * Update this VO using updateThisVO(), and update its kids, recursively, if
     * it is alive
     */
    void updateVO(Stage stage);

    /**
     * Compute the transform for this VO only
     */
    void computeThisTransformVO(World world);

    /**
     * Compute the transform for this VO using computeThisTransformVO(), and do
     * its kids, recursively, if it is alive
     */
    void computeTransformVO(World world);

    /**
     * Draw this VO only. VO pose + fg + bg has already been applied to the
     * v2d.getGraphics().
     */
    void drawThisVO(VOGraphics2D v2d);

    /**
     * If alive, apply Pose, draw this VO using drawThisVO(), and draw its kids,
     * recursively.
     */
    void drawVO(VOGraphics2D g2d);

    /**
     * Add child to the pending children for this VO. WARNING: This child will
     * not be updated until the next update of this VO.
     * 
     * @param child
     */
    void addPendingChild(VO child);

    /**
     * Make tp and add it to the pending trigger points for this VO.
     * 
     */
    TriggerPoint addPendingTriggerPoint(double x, double y);

    /**
     * Add any pending trigger points to this VO and return an iterator over
     * them all
     * 
     * @return
     */
    Iterator<TriggerPoint> getTriggerPointIterator();

    /**
     * Respond to another VO encroaching on 'tp'
     * 
     * @param tp
     *            The TriggerPoint that was covered
     * @param other
     *            The VO that drew over 'tp'
     * @return true to stop processing trigger points on this VO for now; false
     *         to consider other triggerpoints if any
     */
    boolean handleOverlappedTriggerPoint(TriggerPoint tp, VO other);

    /**
     * Get the parent VO of this VO. WARNING 1: There may be no parent. WARNING
     * 2: The parent might be killed or disabled even if this VO is not.
     * 
     * @return The parent VO or null if this VO is (currently) unconnected or
     *         the root.
     */
    VO getParent();

    /**
     * Set the parent of this VO to vo, unless this.getParent()!=null, in which
     * case throw IllegalStateException;
     * 
     * @param vo
     *            non-null parent
     */
    void setParent(VO vo);

    /**
     * Set the parent of this VO to null.
     */
    void clearParent();

    /**
     * Map a position in VO coordinates to the corresponding (but possibly
     * non-existent) {@link Stage} pixel coordinate.
     * 
     * @param inVOC
     *            non-null point in the current VO
     * @param outPixel
     *            if null, a new Point2D.Double is allocated as outPixel; either
     *            way, the resulting pixel coord is then stored into outPixel.
     * @return the supplied or new outPixel holding the resulting pixel coord
     */
    Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel);

    /**
     * Map a pixel position on stage to the corresponding position in VO
     * coordinates (which may or may not actually be within the confines of the
     * rendered VO).
     * 
     * @param inPixel
     *            non-null point in Stage coordinates
     * @param outVOC
     *            if null, a new Point2D.Double is allocated as outPixel way,
     *            the resulting VOC coord is then stored into outPixel.
     * @return null if if this VO currently occupies no area on Stage so
     *         therefore no VOCs exist; otherwise the supplied or new outVOC
     *         holding the resulting pixel coord
     */
    Point2D mapPixelToVOCOrNull(Point2D inPixel, Point2D outVOC);

    /**
     * @return true iff this VO can be considered the 'mouse target VO' for
     *         purposes of receiving mouse events. Return false to be 'mouse
     *         blind'
     */
    boolean isMouseAware();

    /**
     * @return true iff this VO can be considered the 'keyboard focus VO' for
     *         purposes of receiving keyborad events. Return false to be get no
     *         keyboard events.
     */
    boolean isFocusAware();

    /**
     * Notification to a mouse-aware VO that it is now the 'mouse target VO'
     * 
     * @param at
     *            The initial point that the cursor entered this VO
     */
    void mouseEntered(Point2D at);

    /**
     * Notification to a mouse-aware VO that it is no longer the 'mouse target
     * VO'.
     */
    void mouseExited();

    /**
     * Notification to a mouse-aware VO being dragged that the cursor is now at
     * 'at', and this VO should drag itself if necessary to maintain its
     * position relative to that
     * 
     * @param at
     *            current cursor position in stage pixels
     * @param state
     *            >0 for mouse press, <0 for mouse release, ==0 for mouse drag
     */
    void dragAtStage(Point2D at, int state);

    /**
     * Let a wheel-aware VO handle a zoom request of 'amount' located at 'at'
     * 
     * @param at
     *            current cursor position in stage pixels
     * @param amount
     *            >0 zoom in, <0 zoom out
     */
    boolean zoomAround(Point2D at, double amount);

    /**
     * Let a wheel-aware VO handle a rotation request of 'amount' located at
     * 'at'
     * 
     * @param at
     *            current cursor position in stage pixels
     * @param amount
     *            >0 rotate clockwise, <0 rotate anticlockwise
     */
    boolean rotateAround(Point2D at, double amount);

    /**
     * Let this VO handle the mouse event described by mei if it wishes to
     * 
     * @param mei
     *            MouseEventInfo to be potentially handled
     * @return true iff the event has now been handled and propagation should
     *         stop, or false if this VO did not handle the event
     */
    boolean handleMouseEvent(MouseEventInfo mei);

    /**
     * Let this VO handle the keyboard event described by kei if it wishes to
     * 
     * @param kei
     *            KeyboardEventInfo to be potentially handled
     * @return true iff the event has now been handled and propagation should
     *         stop, or false if this VO did not handle the event
     */
    boolean handleKeyboardEvent(KeyboardEventInfo kei);

    /**
     * Let this VO handle the special event described by sei if it wishes to
     * 
     * @param sei
     *            SpecialEventInfo to be potentially handled
     * @return true iff the event has now been handled and propagation should
     *         stop, or false if this VO did not handle the event
     */
    boolean handleSpecialEvent(SpecialEventInfo mei);

    /**
     * Get the 24 bit hitmap color assigned to this VO, extended to 32 bits with
     * alpha=255. For use by Hitmap.
     * 
     * @return the color with alpha==255, if initialized, else 0
     */
    int getHitmapColor();

    /**
     * Set the 24 bit hitmap color assigned to this VO. Can only be called once
     * on a given VO; for use by Hitmap. alpha set to 255 in this method.
     * 
     * @param code
     *            non-zero color value to assign to this VO
     */
    void setHitmapColor(int code);
}
