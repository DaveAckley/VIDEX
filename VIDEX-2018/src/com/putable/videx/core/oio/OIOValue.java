package com.putable.videx.core.oio;

import com.putable.videx.core.oio.load.ParseException;
import com.putable.videx.core.oio.load.AST.ASTValue;
import com.putable.videx.interfaces.OIOAbleGlobalMap;

public class OIOValue implements OIOValueExternalizer, OIOValueInternalizer {

    private final OIOValueExternalizer mPacker;
    private final OIOValueInternalizer mUnpacker;
    private final boolean mNullOK;
    
    public OIOValue(boolean nullOK, OIOValueExternalizer pack, OIOValueInternalizer unpack) {
        if (pack == null || unpack == null)
            throw new IllegalArgumentException();
        this.mNullOK = nullOK;
        this.mPacker = pack;
        this.mUnpacker = unpack;
    }
    
    public OIOValue(boolean nullOK, OIOValue unbox) {
        this(nullOK,unbox.mPacker,unbox.mUnpacker);
    }

    public boolean isNullOK() { return mNullOK; }
    
    @Override
    public Object internalize(ASTValue val, OIOAbleGlobalMap map) {
        if (val.isNull()) { 
            if (this.mNullOK) return null;
            throw new ParseException(val.getToken(), "Null illegal");
        }
        if (this.mNullOK && val.isNull()) return null;
        return mUnpacker.internalize(val, map);
    }

    @Override
    public String externalize(Object val, OIOAbleGlobalMap map, boolean addrefs)
            throws OIOException {
        if (val == null) {
            if (this.mNullOK) return "null";
            throw new OIOException("Null illegal");
        }
        return mPacker.externalize(val, map, addrefs);
    }

}
