package com.putable.videx.core.oio;

import com.putable.videx.core.oio.load.AST.ASTValue;
import com.putable.videx.interfaces.OIOAbleGlobalMap;

public interface OIOValueInternalizer {
    Object internalize(ASTValue val, OIOAbleGlobalMap map) ;
}