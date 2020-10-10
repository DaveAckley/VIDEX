package com.putable.videx.drivers;

import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOLoad;
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
        GlobalOnumMap omap = new GlobalOnumMap();
        OIOLoad oio = new OIOLoad(basedir, omap);
        OIOLoad oioNotes = new OIOLoad(basedir, omap);
        try {    
            mConfiguration = new OIOLoadConfiguration(oio);
            mNotesConfiguration = new OIOLoadNotesConfiguration(oioNotes);
            mWorld = mConfiguration.buildWorld(mConfiguration);
            if (mWorld==null)
                throw new Exception("World construction failed");
            mNotesWorld = mNotesConfiguration.buildNotesWorld(mNotesConfiguration);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    mNotesWorld.runWorld();
                }
                
            }).start();

            mWorld.runWorld();
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
