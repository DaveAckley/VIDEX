package com.putable.videx.std.configurations.slidedeck;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.invoke.MethodHandles;

import com.putable.videx.core.AbstractConfiguration;
import com.putable.videx.drivers.CompilerDriver;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class StandardSlideDeckConfiguration extends AbstractConfiguration {

    public StandardSlideDeckConfiguration(String worldname) {
        super(worldname,new Rectangle2D.Double(0,0,100,100));
    }
    
    @Override
    public World buildWorld(Configuration config) {
        return new StandardSlideDeckWorld(config);
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

    @Override
    public Point2D getStagePanelScale() {
        throw new IllegalStateException();
    }
}
