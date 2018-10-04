package com.putable.videx.std.configurations.slidedeck;

import com.putable.videx.core.StandardWorld;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.Stage;

public class StandardSlideDeckWorld extends StandardWorld {

    public StandardSlideDeckWorld(Configuration config) {
        super(config);
        Stage s = new StandardSlideDeckStage(this,config);
        s.initStage();
        this.addStage(s);
    }
}
