package com.putable.videx.core.oio.save.test;

import java.io.IOException;

import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.std.vo.Square;
import com.putable.videx.std.vo.UnitAxes;
import com.putable.videx.std.vo.WiggleSquare;
import com.putable.videx.std.vo.Slides.BasicSlide;
import com.putable.videx.std.vo.Slides.BasicSlideDeck;

public class OIOTestSave3 {
    private static int onums = 0;
    public static int nextOnum() { return ++onums; } 
    public static void main(String[] args) throws IOException, OIOException {
        GlobalOnumMap omap = new GlobalOnumMap();
        omap.endLoading(); // Starting fresh here
        BasicSlide[] guys = { 
                omap.newOIOAble(BasicSlide.class, null),
                omap.newOIOAble(BasicSlide.class, null),
                omap.newOIOAble(BasicSlide.class, null)
        };
        BasicSlideDeck top = omap.newOIOAble(BasicSlideDeck.class, null);
        guys[0].setSlideName("curly"); 
        guys[1].setSlideName("larry"); 
        guys[2].setSlideName("moe");
        guys[1].addPendingChild(omap.newOIOAble(WiggleSquare.class,null));
        guys[1].addPendingChild(omap.newOIOAble(Square.class,null));
        guys[1].addPendingChild(omap.newOIOAble(UnitAxes.class,null));
        for (BasicSlide bs : guys)
            top.addPendingChild(bs);
        String basedir = "/home/ackley/SPIKES";
        OIOSave os = new OIOSave(basedir);
        os.save(top, omap);
    }

}
