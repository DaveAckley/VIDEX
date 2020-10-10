package com.putable.videx.std.configurations.slidedeck;

import java.lang.invoke.MethodHandles;

import com.putable.videx.core.AbstractConfiguration;
import com.putable.videx.drivers.CompilerDriver;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class StandardSlideDeckConfiguration extends AbstractConfiguration {

    @Override
    public World buildWorld(Configuration config) {
        return new StandardSlideDeckWorld(config);
    }

    @Override
    public World buildNotesWorld(Configuration config) {
        throw new RuntimeException("UNIMPLEMENTED XXX");
        //return new StandardSlideDeckWorld(config);
    }

    @Override
    public String getTitle() {
        return MY_CLASS.getName();
    }

    @Override
    public int getFPS() {
        return 25;
    }

    public static final Class<?> MY_CLASS = MethodHandles.lookup().lookupClass();
    
    public static void main(String[] s) {
            CompilerDriver.main(new String[] { s[0], MY_CLASS.getName() });
    }

    @Override
    public boolean wantFullScreen() {
        return true;
    }
}
