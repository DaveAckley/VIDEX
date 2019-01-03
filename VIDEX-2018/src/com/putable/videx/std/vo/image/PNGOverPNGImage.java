package com.putable.videx.std.vo.image;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.HittableImage;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.utils.FileUtils;

/**
 * A class supporting one PNG drawn on top of another PNG
 * @author ackley
 *
 */
@OIOTop
public class PNGOverPNGImage extends EventAwareVO {

        @OIO
        private String mImageUnderPath = "";

        @OIO
        private double mPercentOver = 25.0;

        @OIO
        private String mImageOverPath = "";

        private HittableImage mImageUnder = null;
        private HittableImage mImageOver = null;

        private void initFromImageDataIfNeededAndAvailable() {
            if (mImageUnder == null) mImageUnder = initIfPossible(mImageUnderPath);
            if (mImageOver == null) mImageOver = initIfPossible(mImageOverPath);
        }
         
        private HittableImage initIfPossible (String imagePath) {
            byte[] data = FileUtils.readWholeFileAsByteArray(Paths.get(imagePath));
            if (data == null) return null;
            HittableImage ret = new HittableImage(this);
            ret.setImageFromBytesIfPossible(imagePath, data);
            ret.setAlphaHittable(false);
            return ret;
        }

        @Override
        public void drawThisVO(VOGraphics2D v2d) {
            Graphics2D g2d = v2d.getGraphics2D();
            initFromImageDataIfNeededAndAvailable();
            if (mImageUnder != null) {
                BufferedImage bi = mImageOver.getImage(v2d);
                g2d.drawImage(bi, 0, 0, bi.getWidth(),
                        bi.getHeight(), null);
            }
            if (mImageOver != null) {                
                BufferedImage bi = mImageOver.getImage(v2d);
                int width = bi.getWidth();
                int showwidth = (int) (this.mPercentOver * width / 100.0);
                Shape hold = g2d.getClip();
                g2d.setClip(new Rectangle(0,0,showwidth,bi.getHeight()));
                g2d.drawImage(bi, 0, 0, bi.getWidth(),
                        bi.getHeight(), null);
                g2d.setClip(hold);
            }
        }

        @Override
        public boolean updateThisVO(Stage stage) {
            return true;
        }

        @Override
        public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
            // By default we do not handle keyboard crap.
            return false;
        }

    }
