package com.putable.videx.interfaces;

import java.awt.geom.Point2D;

import com.putable.videx.core.Hitmap;
import com.putable.videx.core.SXRandom;
import com.putable.videx.std.vo.StageVO;

/**
 * A Stage is all material for interacting with one Display
 * 
 * @author ackley
 * 
 */
public interface Stage {

    /**
     * Get the World this Stage is associated with
     * @return the World
     */
    World getWorld();
    
    /**
     * Do all one-time initialization of this Stage
     */
    void initStage();

    /**
     * Do all processing associated with one step of updating this Stage
     */
    void updateStage(World world);

    /**
     * Compute all transforms for positioning VOs on this State
     */
    void transformStage(World world);

    /**
     * Clean up when finished using this Stage
     */
    void destroyStage();

    /**
     * Get root of VO tree for this Stage
     */
    StageVO getRoot();

    /**
     * Display everything on this Stage however that is done
     */
    void paintStage(World world);

    /**
     * Find the VO that drew pixel in, if any.
     * 
     * @param in
     *            non-null position of pixel on Stage
     * @param out
     *            if non-null and return value non-null, gets corresponding VOC
     *            in returned VO
     * @return null if in is out of bounds or no VO wrote to that position, else
     *         the VO that last wrote to pixel in
     */
    VO mapPixelToVOC(Point2D in, Point2D out);
    
    /**
     * Get the hitmap showing what's where on this stage
     * @return
     */
    Hitmap getHitmap();
    
    /**
     * Access the SXRandom object for this stage
     * @return
     */
    SXRandom getRandom();
}
