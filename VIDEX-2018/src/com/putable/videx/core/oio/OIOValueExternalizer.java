package com.putable.videx.core.oio;

import com.putable.videx.interfaces.OIOAbleGlobalMap;

public interface OIOValueExternalizer {
    String externalize(Object val, OIOAbleGlobalMap map, boolean addrefs) throws OIOException;
}