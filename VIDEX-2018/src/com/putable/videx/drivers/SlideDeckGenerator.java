package com.putable.videx.drivers;

import java.io.IOException;

import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.std.vo.Slides.BasicSlide;
import com.putable.videx.std.vo.Slides.BasicSlideDeck;

public class SlideDeckGenerator {
    private static int onums = 0;
    public static int nextOnum() { return ++onums; } 
    public static void main(String[] args) throws IOException, OIOException {
        if (args.length != 2) throw new IllegalArgumentException("Need two args: BASEDIR SLIDECOUNT");
        String basedir = args[0];
        int slideCount = Integer.parseInt(args[1]);
        GlobalOnumMap omap = new GlobalOnumMap();
        omap.endLoading(); // Starting fresh here
        BasicSlideDeck top = omap.newOIOAble(BasicSlideDeck.class, null);
        for (int i = 0; i < slideCount; ++i) {
            BasicSlide bs = omap.newOIOAble(BasicSlide.class, null);
            bs.setText("<HTML>\n<H1>Slide "+(i+1)+"</H1>\n");
            top.addPendingChild(bs);
        }
        OIOSave os = new OIOSave(basedir);
        os.save(top, omap);
    }
}
