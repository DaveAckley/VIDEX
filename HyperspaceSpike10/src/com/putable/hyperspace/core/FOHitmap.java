package com.putable.hyperspace.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.putable.hyperspace.interfaces.Finite2DSpace;

public class FOHitmap {
	private final Finite2DSpace mF2D;
	private BufferedImage mHitmapImage;
    private int mHitmapWidth;
    private int mHitmapHeight;
    private Graphics2D mHitmapGraphics;
    private boolean mHitmapValid = false;

    public int getHitmapWidth() { return mHitmapWidth; }
    public int getHitmapHeight() { return mHitmapHeight; }
    
    public void paintImage(Graphics2D g2d) {
    	Rectangle2D rect = mF2D.getBounds();
    	g2d.drawImage(mHitmapImage, null, (int) (rect.getMinX()), (int) (5+rect.getMinY()));
    }
    
    public FOHitmap(Finite2DSpace space, int width, int height) {
    	mF2D = space;
    	resizeHitmap(width,height);
    }

    public void resizeHitmap(int width, int height) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Dimensions must be positive");
        mHitmapWidth = width;
        mHitmapHeight = height;
        mHitmapImage = new BufferedImage(mHitmapWidth, mHitmapHeight,
                BufferedImage.TYPE_INT_ARGB);
        mHitmapGraphics = mHitmapImage.createGraphics();
        mHitmapValid = false;
        this.clear();
    }

    public boolean isValid() {
        return mHitmapValid;
    }
    public FO getFOAtPixelIfAny(int x, int y) {
    	Integer idx = getFOIndexAtPixelIfAny(x,y);
    	if (idx == null) return null;
    	return mF2D.getFOForIndex(idx);
    }
    
    public Integer getFOIndexAtPixelIfAny(int x, int y) {
    	if (x < 0 || y < 0) return null;
    	if (x >=  mHitmapWidth || y >= mHitmapHeight) return null;
    	return this.mHitmapImage.getRGB(x, y)&0x00ffffff;
    }
    
    public void clear() {
    	mHitmapGraphics.setColor(new Color(250,200,200));
    	mHitmapGraphics.fillRect(0, 0, mHitmapWidth, mHitmapHeight);
    }

    public Graphics2D getGraphics2D() {
        return mHitmapGraphics;
    }
}
