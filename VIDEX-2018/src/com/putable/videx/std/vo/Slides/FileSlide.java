package com.putable.videx.std.vo.Slides;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.file.attribute.FileTime;

import javax.swing.JLabel;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Fonts;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.utils.FileUtils;

public class FileSlide extends EventAwareVO {
    private Point2D mSlideSize = new Point2D.Double(400, 300);
    private JLabel mText = new JLabel();
    private Point2D mOrigin = new Point2D.Double(5, 5);
    private int mFontSize = 18;
    private Font mFont = Fonts.SINGLETON.getFont("Gillius ADF", Font.PLAIN,
            mFontSize);
    private File mFile = null;
    private FileTime mTimestamp;

    public void setFile(File file) {
        mFile = file;
        mTimestamp = null; // Triggers reload
    }

    public boolean reloadIfNecessary() {
        FileTime time = FileUtils.getModificationTime(mFile.toPath());
        if (time == null)
            throw new IllegalStateException();
        if (mTimestamp == null || time.compareTo(mTimestamp) > 0) {
            mTimestamp = time;
            reload();
            return true;
        }
        return false;
    }

    public void reload() {
        String text = FileUtils.readWholeFile(mFile.toPath());
        if (text == null)
            text = "Read of " + mFile + " failed";
        setText("<HTML>" + text);
    }

    public void setText(String t) {
        mText.setText(t);
    }

    public FileSlide(File file) {
        this.setBackground(Color.black);
        this.setForeground(Color.yellow);
        this.getPose().setS(5);
        this.setFile(file);
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        reloadIfNecessary();
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        g2d.clearRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
        g2d.drawRect(0, 0, (int) mSlideSize.getX(), (int) mSlideSize.getY());
        if (!v2d.isRenderingToHitmap()) {
            Font oldfont = g2d.getFont();
            g2d.setFont(mFont);
            g2d.translate(mOrigin.getX(), mOrigin.getY());
            mText.setSize((int) mSlideSize.getX(), (int) mSlideSize.getY());
            mText.setForeground(this.getForeground());
            mText.setBackground(this.getBackground());
            mText.setFont(this.mFont);
            mText.paint(g2d);
            g2d.translate(-mOrigin.getX(), -mOrigin.getY());
            g2d.setFont(oldfont);
        }
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return false;
    }

}
