package com.putable.videx.drivers;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.putable.videx.core.StandardUniverse;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class OIOLoadDriver2020 {
    private static final long serialVersionUID = 1L;
    private Configuration mConfiguration;
    private Configuration mNotesConfiguration;

    private World mWorld;
    private World mNotesWorld;

    private static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
    public OIOLoadDriver2020(String[] args) {
        if (args.length < 1) die("Need one arg");
        String basedir = args[0];
        StandardUniverse universe = new StandardUniverse();
        try {    
            mConfiguration = 
                    new OIOLoadConfiguration(basedir,"MAIN",
                            true,
                            new Point2D.Double(1,1),
                            new Rectangle2D.Double(0,0,1920*2,1080*2));
            mWorld = mConfiguration.buildWorld(mConfiguration);
            if (mWorld==null)
                throw new Exception("World construction failed");
            universe.addWorld(mWorld);
            final double PRESENTER_SCALE = 0.4;
            mNotesConfiguration = 
                    new OIOLoadConfiguration(basedir,"PRESENTER",
                            false,
                            new Point2D.Double(PRESENTER_SCALE,-PRESENTER_SCALE),
                            new Rectangle2D.Double(5760,38,1024,768-38));
            mNotesWorld = mNotesConfiguration.buildWorld(mNotesConfiguration);
            universe.addWorld(mNotesWorld);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    mNotesWorld.runWorld(universe);
                }
                
            }).start();

            mWorld.runWorld(universe);
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
            new OIOLoadDriver2020(args);
        } catch (Exception e) {
            System.err.println("The world done gone");
            e.printStackTrace();
        }
    }

}
