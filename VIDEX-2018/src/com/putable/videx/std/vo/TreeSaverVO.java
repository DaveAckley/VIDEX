package com.putable.videx.std.vo;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.core.oio.save.OIOSave;
import com.putable.videx.drivers.OIOLoadConfiguration;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.OIOAbleGlobalMap;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;

@OIOTop
public class TreeSaverVO extends EventAwareVO {

    /**
     * Front-running keyboard overrides for TreeSaverVO special purposes.  
     * For the moment, try handling ^S up here, only.
     * 
     * @param kei
     * @return true if we took the event
     */
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        KeyEvent ke = kei.getKeyEvent();
        if (ke.getID() != KeyEvent.KEY_TYPED) return false;
        if (ke.getKeyChar() == '\023') return checkpointSave(); //^S
        return false;
    }


    @Override
    public boolean handleKeyboardEvent(KeyboardEventInfo kei) {
        if (handleKeyboardEventHere(kei)) 
            return true;
        for (VO kid : this) {
            if (kid.handleKeyboardEvent(kei))
                return true;
        }
        return false;
    }

    @OIO(value=OIO.BASE_DIRECTORY)
    private String mOIOBaseDir = null;

    private boolean checkpointSave() {
        World w = this.getWorld();
        if (w == null) throw new IllegalStateException();
        Configuration c = w.getConfiguration();
        if (!(c instanceof OIOLoadConfiguration))
            throw new IllegalStateException("DAVE IS A LOSER");
        OIOLoadConfiguration oc = (OIOLoadConfiguration) c; 
        OIOLoad loader = oc.getLoader();

        File dir = new File(loader.getBaseDirectory());
        //File realbase = dir.getParentFile();
        File realbase = dir; // base IS the realbase?
        
        OIOAbleGlobalMap map = loader.getMap();
        OIOSave os = new OIOSave(realbase.toString());
        VO holdParent = this.getParent();
        this.clearParent();
        try {
            map.endLoading();
            os.save(this, map);
        } catch (IOException | OIOException e) {
            e.printStackTrace();
            System.err.println("StageVO save failed: " + e);
        }
        this.setParent(holdParent);
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // For now the TreeSaver works behind the scenes
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }
}
