package com.putable.videx.interfaces;

public interface RiderGenerator extends OIOAble {
    /**
     * Produce an appropriate subclass of Rider, whatever that means to you. z
     * 
     * @return a non-null pointer to a newly constructured instance of a
     *         subclass of Rider
     */
    Rider generate();
}
