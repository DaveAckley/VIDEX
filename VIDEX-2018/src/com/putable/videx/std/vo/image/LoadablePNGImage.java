package com.putable.videx.std.vo.image;

import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;

@OIOTop
public class LoadablePNGImage extends PNGImage {
    @OIO
    private String mLoadPath = null;
    private boolean mLoadFailed = false;
    
    private void tryLoadImage(String path) {
        if (getImageData() != null || mLoadFailed) return;
        if (mLoadPath == null) return;
        OIOImage temp = OIOImage.makeFromPath(path);
        if (temp == null) {
            mLoadFailed = true;
        } else {
            setImageData(temp.getImageData());
            setImageName(mLoadPath);
        }
    }
    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        tryLoadImage(mLoadPath);
        
        super.drawThisVO(v2d);
    }

}
