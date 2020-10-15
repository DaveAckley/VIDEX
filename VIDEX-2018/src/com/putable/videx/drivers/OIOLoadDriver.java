package com.putable.videx.drivers;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.putable.videx.core.StandardUniverse;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class OIOLoadDriver {
    private static final long serialVersionUID = 1L;
    private Configuration mConfiguration;

    private World mWorld;

    private static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
    public OIOLoadDriver(String[] args) {
        if (args.length < 1) die("Need one arg");
        String basedir = args[0];
        try {    
            mConfiguration = 
                    new OIOLoadConfiguration(basedir, "MAIN", 
                            true, 
                            new Point2D.Double(1,1),
                            new Rectangle2D.Double(0,0,1920*2,1080*2));
            mWorld = mConfiguration.buildWorld(mConfiguration);
            if (mWorld==null)
                throw new Exception("World construction failed");
            mWorld.runWorld(StandardUniverse.makeOneWorldUniverse(mWorld));
        } 
        catch (Exception e) {
            die("Load failure from "+basedir+":\n"+e.toString());
        }
        finally {
            if (mWorld!=null)
                mWorld.destroyWorld();
        }
    }

    public static void main(String[] args) {
        
        try {
            new OIOLoadDriver(args);
        } catch (Exception e) {
            System.err.println("The world done gone");
            e.printStackTrace();
        }
    }

}
