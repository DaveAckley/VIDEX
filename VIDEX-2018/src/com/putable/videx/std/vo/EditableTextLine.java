package com.putable.videx.std.vo;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;

public class EditableTextLine extends EventAwareVO {
    public class TextLineEnteredEventInfo extends SpecialEventInfo{
        private String mValue = null;
        public void setValue(String s) {
            mValue = s;
        }

        @Override
        public Object getValue() {
            return mValue;
        }
        
    }
    {
        this.setIsFocusAware(true);
    }
    @OIO(owned=false)
    private VO mCallbackVO = null;
    
    public void setCallback(VO callback) {
        mCallbackVO = callback;
    }
    
    @OIO
    private String mText = "";

    public void setText(String str) {
        if (str == null)
            throw new IllegalArgumentException();
        if (str.matches("[\\\\n]"))  // This is not enough, right?
            throw new IllegalArgumentException();
        mText = str;
    }

    public String getText() {
        return mText;
    }
    
    @OIO
    /**
     * 0: Before first char
     * mText.length(): After last char
     */
    private int mCursorPos = 0;

    private int mCursorPhase = 0;
    private final static int CURSOR_PHASE_MAX = 40; 

    private final static int MIN_WIDTH = 10; 

    @Override
    public boolean updateThisVO(Stage s) {
        if (++mCursorPhase >= CURSOR_PHASE_MAX)
            mCursorPhase = 0;
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        FontMetrics fm = getUnscrewedFontMetrics(g2d, 0);
        //System.out.println("DDKIE "+fm.getAscent()+" "+fm.getDescent());
        int height = fm.getAscent() + fm.getDescent();
        g2d.clearRect(0, -fm.getDescent(), Math.max(MIN_WIDTH, fm.stringWidth(mText)), height);
        g2d.drawString(mText, 0, fm.getAscent()-fm.getDescent());
        if (mCursorPhase < CURSOR_PHASE_MAX/2 && mCursorPos >= 0 && mCursorPos <= mText.length()) {
            String substr = mText.substring(0, mCursorPos);
            int wid = fm.stringWidth(substr);
            g2d.fillRect(wid, -fm.getDescent(), 1, height);
        }
    }

    public EditableTextLine() { this(null,0,0); }

    public EditableTextLine(VO callback, int x, int y) {
        this.setCallback(callback);
        this.setBackground(Color.BLUE);
        this.setForeground(Color.WHITE);
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
    }

    @Override
    public Point2D mapVOCToPixel(Point2D inVOC, Point2D outPixel) {
        throw new UnsupportedOperationException("NYI");
    }

    private boolean handleKeyCodeForEdit(int code,boolean insrt) {
        if (!insrt) switch (code) {
        case KeyEvent.VK_LEFT: return moveCursorRelative(-1);
        case KeyEvent.VK_RIGHT: return moveCursorRelative(1);
        case KeyEvent.VK_HOME: 
        case KeyEvent.VK_PAGE_UP: 
            return moveCursorRelative(Integer.MIN_VALUE);
        case KeyEvent.VK_END: 
        case KeyEvent.VK_PAGE_DOWN: 
            return moveCursorRelative(Integer.MAX_VALUE);
        case KeyEvent.VK_DELETE: return deleteRelative(1);
        case KeyEvent.VK_BACK_SPACE: return deleteRelative(-1);
        default: break;
        }
        if (!Character.isBmpCodePoint(code))
            return false; // Sorry can't deal with unicode yet
        if (Character.isISOControl(code)) {
            if (code == '\n') {
                if (mCallbackVO != null) {
                    TextLineEnteredEventInfo info = new TextLineEnteredEventInfo();
                    info.setValue(this.mText);
                    mCallbackVO.handleSpecialEvent(info);
                }
                this.killVO();
            }
            return false;
        }
        return !insrt || insertPrintable((char) code);
    }
    
    private boolean moveCursorRelative(int i) {
        int newpos = mCursorPos + i; 
        if (newpos < 0)
            mCursorPos = 0;
        else if (newpos > mText.length())
            mCursorPos = mText.length();
        else
            mCursorPos = newpos;
        return true;
    }

    private String getPre() {
        if (mCursorPos <= 0) return "";
        if (mCursorPos >= mText.length()) return mText;
        return mText.substring(0, mCursorPos);
    }
    
    private String getPost() {
        if (mCursorPos <= 0) return mText;
        if (mCursorPos >= mText.length()) return "";
        return mText.substring(mCursorPos, mText.length());
    }

    private boolean deleteRelative(int i) {
        String pre = getPre();
        String post = getPost();
        if (i < 0) {
            int newlen = pre.length() + i;
            if (newlen > 0)
                pre = pre.substring(0, newlen);
            else
                pre = "";
        } else if (i > 0) {
            int newlen = post.length() - i;
            if (newlen > 0)
                post = post.substring(i, post.length());
            else
                post = "";
        }
        mText = pre + post;
        mCursorPos = pre.length();
        return true;
    }


    private boolean insertPrintable(char ch) {
        String pre = getPre();
        String post = getPost();
        mText = pre + ch + post;
        mCursorPos = pre.length()+1;
        return true;
    }
    
    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        if (isMouseTarget()) {
            KeyEvent ke = kei.getKeyEvent();
            if (ke.getID() == ke.KEY_TYPED) {
                char ch = ke.getKeyChar();
                if (handleKeyCodeForEdit(ch,true))
                    return true;
            }
            if (ke.getID() == ke.KEY_RELEASED) {
                int code = ke.getKeyCode();
                if (handleKeyCodeForEdit(code,false))
                    return true;
            }
        }
        return false;
    }
}
