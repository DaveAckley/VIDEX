package com.putable.videx.core.oio.save.test;

import java.io.IOException;

import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.interfaces.OIOAble;

public class OIOTestSave {
    private static int onums = 0;
    public static int nextOnum() { return ++onums; } 
    public static void main(String[] args) throws IOException, OIOException {
        OIOAble[] guys = { new MyGuy1(), new MyGuy2(), new MyGuy1() };
        String basedir = "/home/ackley/SPIKES";
        GlobalOnumMap omap = new GlobalOnumMap();
        OIOSave os = new OIOSave(basedir);
        throw new UnsupportedOperationException("DMPLEMTARY");
        //os.saveAll(Arrays.asList(guys),omap);
    }

}
