package com.putable.videx.std.vo;

import com.putable.videx.core.oio.OIO;
import com.putable.videx.core.oio.OIOTop;

@OIOTop
public class JPGImage extends OIOImage {
    @OIO(inline = false, extension = "jpg")
    private byte[] mData = null;

    public JPGImage() { }
    public JPGImage(byte[] data) {
        mData = data;
    }

    @Override
    public byte[] getImageData() {
        return mData;
    }

}
