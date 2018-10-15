package com.putable.videx.std.vo;

import java.awt.geom.Point2D;

import com.putable.videx.core.MouseVO;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.MouseEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;

public class StageVO extends StandardVO {
    private MouseVO mMouseVO = new MouseVO();
    private final World mWorld;
    
    public World getWorld() {
        return mWorld;
    }
    
    public MouseVO getMouseVO() {
        return mMouseVO;
    }

    private VO mGlassLayerRoot = mMouseVO;

    @Override
    public void computeThisTransformVO(World world) {
        super.computeThisTransformVO(world);
        mGlassLayerRoot.computeTransformVO(world);
    }

    @Override
    public void drawVO(VOGraphics2D v2d) {
        super.drawVO(v2d);
        // Put glass layer last
        mGlassLayerRoot.drawVO(v2d);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        mGlassLayerRoot.updateVO(stage);
        return true;
    }

    public StageVO(World w) {
        if (w == null) throw new IllegalArgumentException();
        mWorld = w;
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        /* At the Stage level, this is an identity mapping by definition */
        if (outPixel == null)
            outPixel = new Point2D.Double();
        outPixel.setLocation(inVOC);
        return outPixel;
    }

    @Override
    public boolean isMouseAware() {
        return false;
    }

    @Override
    public boolean isFocusAware() {
        return false;
    }

    @Override
    public void mouseEntered(Point2D at) {
        /* Nothing */
    }

    @Override
    public void mouseExited() {
        /* Nothing */
    }

    @Override
    public boolean handleMouseEvent(MouseEventInfo mei) {
        return false;
    }

    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        for (VO kid : this) {
            if (kid.handleKeyboardEvent(kei))
                return true;
        }
        return false;
    }

    @Override
    public boolean handleSpecialEvent(SpecialEventInfo mei) {
        return false;
    }

    @Override
    public void dragAtStage(Point2D at, int state) {
        throw new IllegalStateException("StageVO is not draggable");
    }

    @Override
    public boolean rotateAround(Point2D at, double amount) {
        throw new IllegalStateException("StageVO does not rotate");
    }

    @Override
    public boolean zoomAround(Point2D at, double amount) {
        throw new IllegalStateException("StageVO does not zoom");
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // Nothing directly to do to draw the stage itself
    }
}
