package com.putable.videx.interfaces;

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

    World buildWorld(Configuration config);

    World buildNotesWorld(Configuration config);

    String getTitle();
    
    boolean wantFullScreen();

    int getFPS();
}
