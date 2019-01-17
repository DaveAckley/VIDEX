package com.putable.videx.std.vo.image;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;

@OIOTop
public class PNGImage extends OIOImage {
    @OIO(inline = false, extension = "png")
    private byte[] mData = null;
    protected void setImageData(byte[] data) {
        if (mData != null) throw new IllegalStateException();
        mData = data;
    }
    public PNGImage() { }
    public PNGImage(byte[] data) {
        mData = data;
    }
    
    @Override
    public byte[] getImageData() {
        return mData;
    }

}
