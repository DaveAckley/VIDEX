package com.putable.videx.interfaces;

import com.putable.videx.core.oio.load.AST.ASTValue;

public interface MemberSetter {
    void set(OIOAble obj, ASTValue val, OIOAbleGlobalMap refs);
}
