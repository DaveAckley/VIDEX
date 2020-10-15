package com.putable.videx.core;

import java.awt.geom.Rectangle2D;

import com.putable.videx.interfaces.Configuration;

public abstract class AbstractConfiguration implements Configuration {

    private String[] mArgs = null;

    private final String mWorldName;
    private final Rectangle2D mDesiredWindow;

    @Override
    public Rectangle2D getDesiredWindow() {
        return mDesiredWindow;
    }


    @Override
    public String getWorldName() {
        return mWorldName;
    }


    public AbstractConfiguration(String worldname, Rectangle2D window) {
        super();
        mWorldName = worldname;
        mDesiredWindow = window;
    }

    @Override
    public String[] theArguments(String[] args) {
        if (args != null) mArgs = args;
        return mArgs;
    }

}