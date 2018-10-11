package com.putable.videx.std.vo;

import com.putable.videx.core.oio.OIO;

public class JPGImage extends OIOImage {
    @OIO(inline = false, extension = "jpg")
    private byte[] mData = null;

    @Override
    public byte[] getImageData() {
        return mData;
    }

}
