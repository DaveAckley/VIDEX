package com.putable.videx.interfaces;

/**
 * A collection of Worlds, accessible by name
 * @author ackley
 *
 */
public interface Universe extends Iterable<World> {
    World getWorld(String name);
}
