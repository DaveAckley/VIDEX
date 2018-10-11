package com.putable.videx.std.vo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.HittableImage;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public abstract class OIOImage extends EventAwareVO {
    @OIO
    private String mImageName = "";

    /**
     * Obtain image data for this image
     * 
     * @return New data for the entire image, if it exists, else null
     */
    public abstract byte[] getImageData();

    private HittableImage mImage = null;

    private void initFromImageDataIfNeededAndAvailable() {
        if (mImage != null) return;
        byte[] data = this.getImageData();
        if (data == null)
            return;
        mImage = new HittableImage(this);
        mImage.setImageFromBytesIfPossible(this.mImageName, data);
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        Graphics2D g2d = v2d.getGraphics2D();
        initFromImageDataIfNeededAndAvailable();
        if (mImage != null) {
            BufferedImage bi = mImage.getImage(v2d);
            v2d.getGraphics2D().drawImage(bi, 1, 0, bi.getWidth(),
                    bi.getHeight(), null);
        }
    }

    @Override
    public boolean updateThisVO(Stage stage) {
        return true;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        return true;
    }

}