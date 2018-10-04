package com.putable.videx.interfaces;

public interface SerDesAbleGlobalMap {
    /**
     * Access the SerDesAble that has the given onum, if one exists anywhere in creation
     * @param onum
     * @return null or the SerDesAble
     */
    SerDesAble get(int onum);
}
