package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class PopupTextLineEntry extends EventAwareVO {
    @OIO(owned=false)
    private VO mCallbackVO = null;
    
    public void setCallback(VO callback) {
        mCallbackVO = callback;
    }
    
    public Label getLabel() {
        Label l = this.findFirstInstance(Label.class);
        if (l==null) {
            l = new Label();
            this.addPendingChild(l);
        }
        return l;
    }

    public EditableTextLine getEditableTextLine() {
        EditableTextLine etl = this.findFirstInstance(EditableTextLine.class);
        if (etl == null) {
            etl = new EditableTextLine(this,0,20); // ???
            this.addPendingChild(etl);
        }
        return etl;
    }

    public void setLabel(String str) {
        Label label = this.getLabel();
        if (label == null) {
            label = new Label(0,0);
            this.addPendingChild(label);
        }
        label.setLabel(str);
    }
    
    public void setEditableText(String str) {
        EditableTextLine etl = this.getEditableTextLine();
        etl.setText(str);
    }
    
    public PopupTextLineEntry() { this(0,0); }

    public PopupTextLineEntry(VO callback, String label, String inittext) {
        this();
        this.setLabel(label);
        this.setEditableText(inittext);
        this.setCallback(callback);
    }
    
    public PopupTextLineEntry(int x, int y) {
        this.setBackground(Color.BLUE);
        this.setForeground(Color.WHITE);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        // Nothing here, just draw the kids
    }

    @Override
    public boolean handleSpecialEvent(SpecialEventInfo mei) {
        if (mei instanceof EditableTextLine.TextLineEnteredEventInfo) {
            if (this.mCallbackVO != null)
                this.mCallbackVO.handleSpecialEvent(mei);
            this.killVO();
            return true;
        }
        return false;
    }

}
