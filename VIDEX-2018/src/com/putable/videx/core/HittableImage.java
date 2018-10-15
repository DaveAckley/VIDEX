package com.putable.videx.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.putable.videx.interfaces.VO;


public class HittableImage {

    private final VO mForVO;
    private BufferedImage mImage = null;
    private BufferedImage mHitmapImage = null;

    private void initHitmapImage(VOGraphics2D v2d) {
        if (mHitmapImage != null) throw new IllegalStateException();
        int w = mImage.getWidth();
        int h = mImage.getHeight();
        mHitmapImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mHitmapImage.createGraphics();
        g2d.setColor(new Color(0,true));
        g2d.fillRect(0, 0, w, h);
        int hittable = v2d.getGraphics2D().getColor().getRGB();
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int pix = mImage.getRGB(x, y);
                int alpha = (pix>>24) & 0xff;
                if (alpha > 127)
                    mHitmapImage.setRGB(x, y, hittable);
            }
        }
    }
    
    public void setImageFromBytesIfPossible(String sourceLabel, byte[] bytes) {
        try {
            mImage = ImageIO.read(new ByteArrayInputStream(bytes));
        }
        catch (IOException e) {
            mImage = new BufferedImage(500,100,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mImage.createGraphics();
            g2d.drawString("IMAGE DATA READ FAILED FOR: "+sourceLabel, 10, 10);
        }
        mHitmapImage = null; // Force rebuild
        
    }
    public void setImageFromPathIfPossible(String path) {
        try {
            mImage = ImageIO.read(new File(path));
        }
        catch (IOException e) {
            mImage = new BufferedImage(500,100,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mImage.createGraphics();
            g2d.drawString("IMAGE LOAD FAILED FOR: "+path, 10, 10);
        }
        mHitmapImage = null; // Force rebuild
    }

    public HittableImage(VO forVO) {
        this.mForVO = forVO;
    }

    public HittableImage(VO forVO, String path) {
        this(forVO);
        setImageFromPathIfPossible(path);
    }
    
    
    public BufferedImage getImage(VOGraphics2D v2d) {
        if (v2d.isRenderingToHitmap()) {
            if (mHitmapImage == null) initHitmapImage(v2d);
            return mHitmapImage;
        }
        return mImage;
    }

}
