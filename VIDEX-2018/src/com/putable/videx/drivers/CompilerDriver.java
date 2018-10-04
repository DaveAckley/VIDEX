package com.putable.videx.drivers;

import javax.swing.JFrame;

import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.World;

public class CompilerDriver extends JFrame {
    private static final long serialVersionUID = 1L;
    private Configuration mConfiguration;

    private World mWorld;

    private static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
    public CompilerDriver(String[] args) {
        if (args.length < 1) die("Need at least arg");
        String deckdir = args[0];
        String configClassName;
        if (args.length > 1) {
            if (args.length > 2) die("Need at most two args");
            configClassName = args[1];
        } else 
            configClassName = com.putable.videx.std.configurations.slidedeck.StandardSlideDeckConfiguration.class.getName();
            
        try {    
            Class<?> clazz = Class.forName(configClassName);
            Object object = clazz.newInstance();
            if (!(object instanceof Configuration))
                throw new Exception(configClassName + " is not a Configuration");
            mConfiguration = (Configuration) object;
            mConfiguration.theArguments(args);
            mWorld = mConfiguration.buildWorld(mConfiguration);
            if (mWorld==null)
                throw new Exception("World construction failed");
//            for (Stage s : mWorld) {
//                OIOCompiler compiler = new OIOCompiler(deckdir,s);
//                compiler.reload();
//            }
            mWorld.runWorld();
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
        new CompilerDriver(args);
    }

}
