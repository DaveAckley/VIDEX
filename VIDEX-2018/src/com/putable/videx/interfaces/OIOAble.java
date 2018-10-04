package com.putable.videx.interfaces;

public interface OIOAble {

    /**
     * Get the (positive) object number uniquely associated with this OIOAble.
     * 
     * @return the object number, or -1 if no object number has yet been
     *         assigned to this OIOAble
     */
    int getOnum();

    /**
     * Set the (positive) object number uniquely associated with this OIOAble.
     * (Method for use by GlobalOnumMap only.)
     */
    void setOnum(int onum);
    
    /**
     * Configure all persistent members of yourself based on the ASTDefs in
     * yourMap plus the references in refs
     * 
     * @param yourMap
     *            the Iterable<ASTDef> describing the members you wrote in your
     *            {@link #write(Writer)} method
     * @param refs
     *            the map of all known onums->oioable instances, for setting up
     *            any reference you may have. Note that although all oioables
     *            will have been instantiated by the time
     *            {@link #configureSelf(ASTObj, OIOAbleGlobalMap)} is called,
     *            they will not necessarily have been configured yet: You can
     *            expect any onum references to point to the type you need, but
     *            you cannot count on any of the content of the referred-to
     *            oioable.
     */
    // void configureSelf(ASTObj yourMap, OIOAbleGlobalMap refs);

    /**
     * Write all persistent members of yourself out to w in OIO notation, and
     * add any involved persistent ref members to refs
     * 
     * @param w
     * @param refs
     * @throws IOException
     */
    // void writeOIO(Writer w, OIOAbleGlobalMap refs) throws IOException;

}
