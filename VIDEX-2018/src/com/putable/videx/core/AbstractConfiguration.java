package com.putable.videx.core;

import com.putable.videx.interfaces.Configuration;

public abstract class AbstractConfiguration implements Configuration {

    private String[] mArgs = null;

    public AbstractConfiguration() {
        super();
    }

    @Override
    public String[] theArguments(String[] args) {
        if (args != null) mArgs = args;
        return mArgs;
    }

}