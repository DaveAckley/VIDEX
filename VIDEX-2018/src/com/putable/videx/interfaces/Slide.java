package com.putable.videx.interfaces;

public interface Slide extends OIOAble {
    /**
     * Get and/or set the Slide HTML component
     * 
     * @param html
     *            if non-null, the HTML to be stored for later
     * 
     * @return the most-recent non-null value of html provided (including the
     *         in-progress invocation, if html is non-null)
     */
  
    String theHTML(String html);
    
    /**
     * Get the name of the Slide 
     */
    String getSlideName();
    
    /**
     * Inform a slide of the status of the deck it is part of.
     * @param sd The deck that this slide is part of
     * @param currentNum The slide number currently 'displayed'
     * @param yourNum The number of this slide within the deck
     * @param totalSlides The number of slides in the deck
     */
    void deckStatus(SlideDeck sd, int currentNum, int yourNum, int totalSlides);

}
