package com.putable.videx.interfaces;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A Configuration provides all the pieces of a particular VIDEX presentation
 * 
 * @author ackley
 *
 */
public interface Configuration {

    /**
     * Get and/or set the configuration arguments
     * 
     * @param args
     *            if non-null, the arguments to be stored for later
     * 
     * @return the most-recent non-null value of args provided (including the
     *         in-progress invocation, if args is non-null)
     */
    String[] theArguments(String[] args);

    String getWorldName(); 
    
    World buildWorld(Configuration config);

// deprecated    World buildNotesWorld(Configuration config, String name);

    String getTitle();
    
    boolean wantFullScreen();

    Point2D getStagePanelScale();

    Rectangle2D getDesiredWindow();
    
    int getFPS();
}
