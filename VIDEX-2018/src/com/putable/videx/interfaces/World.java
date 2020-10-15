package com.putable.videx.interfaces;

import com.putable.videx.core.SXRandom;

/**
 * A World is the object built from a Configuration
 * 
 * @author ackley
 *
 */
public interface World extends Iterable<Stage> {
    String getName();
    
    boolean addStage(Stage stage);

    Stage removeStage(Stage stage);

    void destroyWorld();

    void runWorld(Universe u);

    Universe getUniverseOrNull();
    
    SXRandom getRandom();
    
    Configuration getConfiguration();
}
