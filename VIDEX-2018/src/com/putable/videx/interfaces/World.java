package com.putable.videx.interfaces;

import com.putable.videx.core.SXRandom;

/**
 * A World is the object build from a Configuration
 * 
 * @author ackley
 *
 */
public interface World extends Iterable<Stage> {
    boolean addStage(Stage stage);

    Stage removeStage(Stage stage);

    void destroyWorld();

    void runWorld();

    SXRandom getRandom();
}
