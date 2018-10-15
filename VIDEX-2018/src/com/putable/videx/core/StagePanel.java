package com.putable.videx.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JPanel;

import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.vo.StageVO;

public class StagePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private StageVO mRoot;
    private int mCanvasWidth;
    private int mCanvasHeight;
    private StageGraphics2D mStageGraphics;

    private Point2D mMousePosition = null; // null before we have actually seen
                                           // the mouse
    private boolean mIsDragging = false;
    private VO mMouseTarget = null;

    private void checkTriggerPoints(VO vo, Hitmap hitmap) {
        if (vo == null) return;
        Point2D pt = null; // Reusable point for triggers
        for (Iterator<TriggerPoint> itp = vo.getTriggerPointIterator(); 
                itp.hasNext();) {
            TriggerPoint tp = itp.next();
            if (tp.isKilled()) {
                itp.remove();
            } else {
                pt = tp.getTriggerPoint(pt); // allocates on first call
                vo.mapVOCToPixel(pt, pt);
                VO check = hitmap.getVOForPixel((int) pt.getX(),
                        (int) pt.getY());
                if (check != null && check != vo) {
                    if (vo.handleOverlappedTriggerPoint(tp, check))
                        break;
                }
            }
        }
        // Then do the kids
        for (VO kid : vo) checkTriggerPoints(kid, hitmap);
    }

    /** Custom drawing codes */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Paint background
        //this.requestFocus(); // eats my ESC
        mStageGraphics.startStageRender((Graphics2D) g);
        mRoot.drawVO(mStageGraphics);
        mStageGraphics.finishStageRender();
        // We are now 'technically' in between paintStage and updateStage.
        // Recheck who is under the existing mouse position now
        checkAndUpdateMouseTarget(mMousePosition);

        // Now check for triggering object interactions
        checkTriggerPoints(mRoot, mStageGraphics.getHitmap());
    }

    private void reportMouseAction() {
        // Notify the cursor about every potential position change
        mRoot.getMouseVO().track(mMousePosition);
    }

    private void checkAndUpdateMouseTarget(Point2D at) {

        if (at == null) { // If no mouse point
            if (mMouseTarget != null) { // Then should have no mouse target
                System.out.println("TARGET CHANGE FROM " + mMouseTarget
                        + " to null AT " + at);
                mMouseTarget.mouseExited(); // So clear it
                mMouseTarget = null;
            }
            return; // Beyond that, nothing to do (?)
        }

        this.mMousePosition = at;
       
        reportMouseAction();
    
        // We have a target.
        // First issue: Are we dragging?
        if (mIsDragging) {
            // We are. That means (1) we should have a target, and (2) the
            // target will not change in this method
            if (mMouseTarget == null) {
                System.err.println("DRAGGING WITH NO TARGET AT " + at);
                mIsDragging = false; // ??
                return; // ??
            }

            // Tell the drag target where the mouse is
            System.out.println("AX1 " + at);

            mMouseTarget.dragAtStage(at, 0);
            return;
        }

        // We are not dragging. Has the target changed?
        Rectangle2D r = new Rectangle2D.Double(0, 0, mCanvasWidth,
                mCanvasHeight);
        if (r.contains(at)) {
            Hitmap hm = this.mStageGraphics.getHitmap();
            VO mouser = hm.getVOForPixel((int) mMousePosition.getX(),
                    (int) mMousePosition.getY());
            while (mouser != null && !mouser.isMouseAware())
                mouser = mouser.getParent();
            if (mouser != mMouseTarget) {
                System.out.println("TARGET CHANGE FROM " + mMouseTarget
                        + " to " + mouser + " AT " + at);
                if (mMouseTarget != null)
                    mMouseTarget.mouseExited();
                mMouseTarget = mouser;
                if (mMouseTarget != null) {
                    Point2D voc = mMouseTarget.mapPixelToVOCOrNull(at, null);
                    if (voc == null )
                        throw new IllegalStateException(
                                "This 'can't happen' because mouser just claimed a pixel..");
                    System.out.println("AT " + voc);
                    mMouseTarget.mouseEntered(voc);
                }
            }
        }

    }

    public void transformStage(World w) {
        mRoot.computeTransformVO(w);
    }

    /** Called back to get the preferred size of the component. */
    @Override
    public Dimension getPreferredSize() {
        return (new Dimension(mCanvasWidth, mCanvasHeight));
    }

    public void dispatchKeyEvent(KeyEvent ke) {
        KeyboardEventInfo kei = new KeyboardEventInfo(ke);
        this.mRoot.handleKeyboardEvent(kei);
    }
    
    private void dispatchMouseEvent(MouseEventInfo mei) {
        MouseEvent me;
        MouseWheelEvent mwe;
        
        if ((me = mei.getMouseEvent()) != null) {
            int evtid = me.getID();

            // First do target updates
            Point2D at = me.getPoint();
            if (evtid == MouseEvent.MOUSE_EXITED) // If leaving the whole stage,
                                                  // flag we'e off
                at = null;
            checkAndUpdateMouseTarget(at);

            // Second manage drag starting and stopping
            // Process and consume mouse-dragging-related events
            if (at != null && mMouseTarget != null) {
                // Check for Ctrl-Button1 press - drag start
                int modex = me.getModifiersEx();
                final int dragStartMask = InputEvent.CTRL_DOWN_MASK;
                if (evtid == MouseEvent.MOUSE_PRESSED
                        && me.getButton() == MouseEvent.BUTTON1
                        && ((modex & dragStartMask) != 0)) {
                    mIsDragging = true;
                    System.out.println("AX2");
                    mMouseTarget.dragAtStage(at, 1);
                    return;
                }

                // Check for drag ending
                if (mIsDragging && evtid == MouseEvent.MOUSE_RELEASED
                        && me.getButton() == MouseEvent.BUTTON1) {
                    mIsDragging = false;
                    System.out.println("AX3");
                    mMouseTarget.dragAtStage(at, -1);
                    return;
                }
            }
        } else if ((mwe = mei.getMouseWheelEvent()) != null) {

            int evtid = mwe.getID();
            Point2D at = mwe.getPoint();

            if (evtid != MouseEvent.MOUSE_WHEEL || at == null) {
                System.out.println("WHAWHEEL? " + mwe);
                return;
            }

            double wheelrot = mwe.getPreciseWheelRotation();
            // Check for Dispatch wheel event to mouse target
            if (mMouseTarget != null) {
                // Ctrl-Wheel: Zoom; Ctrl-Shift-Wheel: Rotate
                int modex = mwe.getModifiersEx();
                final int rotateMask = InputEvent.CTRL_DOWN_MASK
                        | InputEvent.SHIFT_DOWN_MASK;
                final int zoomMask = InputEvent.CTRL_DOWN_MASK;
                if ((modex & rotateMask) == rotateMask) {
                    if (mMouseTarget.rotateAround(at, wheelrot)) {
                        reportMouseAction();
                        return;
                    }
                } else if ((modex & zoomMask) == zoomMask) {
                    if (mMouseTarget.zoomAround(at, -wheelrot)) {
                        reportMouseAction();
                        return;
                    }
                }
            }
            // ELSE FALL THROUGH
        }

        // No special case took it. Try general dispatching
        VO handler = mMouseTarget;
        while (handler != null) {
            if (handler.handleMouseEvent(mei))
                return;
            handler = handler.getParent();
        }
        // System.out.println("UNNHANDLED: "+mei);
        return;
    }

    private void dispatchMouseEvent(MouseEvent me) {
        dispatchMouseEvent(new MouseEventInfo(me));
    }

    private void dispatchMouseWheelEvent(MouseWheelEvent mwe) {
        dispatchMouseEvent(new MouseEventInfo(mwe));
    }

    /**
     * Build a Stage
     * @param hm the hitmap to use and update
     * @param root the root of the tree of objects on this stage 
     * @param width the (initial) canvas width
     * @param height the (initial) canvas height
     */
    public StagePanel(Hitmap hm, StageVO root, int width, int height) {
        mStageGraphics = new StageGraphics2D(hm);
        mRoot = root;
        mCanvasWidth = width;
        mCanvasHeight = height;
        mStageGraphics.resizeHitmap(width, height);
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);

        // Handling window resize.
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = (Component) e.getSource();
                Dimension dim = c.getSize();
                StagePanel.this.mCanvasWidth = dim.width;
                StagePanel.this.mCanvasHeight = dim.height;
                StagePanel.this.mStageGraphics.resizeHitmap(dim.width,
                        dim.height);
            }
        });

        // Handle mouse events
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                dispatchMouseEvent(e);
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                dispatchMouseWheelEvent(e);
            }

        };
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        this.addMouseWheelListener(ma);

    }

}