package com.putable.videx.std.vo.Slides;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.putable.videx.core.HittableImage;
import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.oio.OIO;

public class BasicSlidePNGImage extends BasicSlide {
    @OIO(inline = false, extension = "png")
    private byte[] mPngData = null;
    @OIO
    private Pose mImagePose = new Pose();
    
    private HittableImage mImage = null;
    private void initFromPNGDataIfAvailable() {
        if (mPngData == null) return;
        if (mImage == null) mImage = new HittableImage(this);
        mImage.setImageFromBytesIfPossible(this.getSlideName(), mPngData);
        mPngData = null;
    }
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        super.drawThisVO(v2d);
        Graphics2D g2d = v2d.getGraphics2D();
        if (v2d.isRenderingToHitmap())
            return;
        initFromPNGDataIfAvailable();
        if (mImage != null) {
            BufferedImage bi = mImage.getImage(v2d);
            v2d.getGraphics2D().drawImage(bi, 0, 0, bi.getWidth(), bi.getHeight(), null);
        }
    }

}
