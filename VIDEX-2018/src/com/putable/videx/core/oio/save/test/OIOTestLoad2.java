package com.putable.videx.core.oio.save.test;

import java.io.IOException;

import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.interfaces.OIOAble;

public class OIOTestLoad2 {
    private static int onums = 0;
    public static int nextOnum() { return ++onums; }
    public static void main(String[] args) throws IOException, OIOException {
        String basedir = "/home/ackley/SPIKES";
        GlobalOnumMap omap = new GlobalOnumMap();
        OIOLoad oio = new OIOLoad(basedir, omap);
        OIOAble loaded = oio.load();
        System.out.println("Top is " + loaded + " so do something");
    }

}
