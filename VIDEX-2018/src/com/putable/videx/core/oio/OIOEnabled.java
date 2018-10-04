package com.putable.videx.core.oio;

public interface OIOEnabled {
    /**
     * @return Positive object number or -1 if not initialized
     */
    int getOnum();

    /**
     * @return String name of this OIOEnabled, if any, or null
     */
    String getName();
}
