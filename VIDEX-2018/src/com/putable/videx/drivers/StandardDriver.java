package com.putable.videx.drivers;

import javax.swing.JFrame;

import com.putable.videx.core.StandardUniverse;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class StandardDriver extends JFrame {
    private static final long serialVersionUID = 1L;
    private Configuration mConfiguration;

    private World mWorld;

    private static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
    public StandardDriver(String[] args) {
        if (args.length < 1) die("Need one arg");
        String configClassName = args[0];
        try {    
            Class<?> clazz = Class.forName(configClassName);
            Object object = clazz.newInstance();
            if (!(object instanceof Configuration))
                throw new Exception(configClassName + " is not a Configuration");
            mConfiguration = (Configuration) object;
            mWorld = mConfiguration.buildWorld(mConfiguration);
            if (mWorld==null)
                throw new Exception("World construction failed");
            mWorld.runWorld(StandardUniverse.makeOneWorldUniverse(mWorld));
        } 
        catch (Exception e) {
            die("Can't instantiate "+configClassName+":\n"+e.toString());
        }
        finally {
            if (mWorld!=null)
                mWorld.destroyWorld();
        }
    }

    public static void main(String[] args) {
        new StandardDriver(args);
    }

}
