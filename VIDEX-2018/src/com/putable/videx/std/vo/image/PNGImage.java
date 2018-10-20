package com.putable.videx.std.vo.image;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;

@OIOTop
public class PNGImage extends OIOImage {
    @OIO(inline = false, extension = "png")
    private byte[] mData = null;

    public PNGImage() { }
    public PNGImage(byte[] data) {
        mData = data;
    }
    
    @Override
    public byte[] getImageData() {
        return mData;
    }

}
