package com.putable.videx.std.vo.Slides;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public class SlideDeck extends EventAwareVO {
    
    private HTMLEditorKit mKit = new HTMLEditorKit();

    @OIO(inline=false,extension="css")
    private String mCSS = "hewo";
    private String mInstalledCSS = null;
    
    private void checkLoadCSS() {
        if (this.mCSS != null && this.mCSS != this.mInstalledCSS) {
            StyleSheet s = mKit.getStyleSheet();
            s.addRule(this.mCSS);
            this.mInstalledCSS = this.mCSS;
        }
    }
    
    public void setCSS(String css) {
        this.mCSS = css;
        checkLoadCSS();
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        checkLoadCSS();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // SlideDeck is currently invisible but that might change
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        throw new UnsupportedOperationException("XXX WRITE ME");
    }

}
