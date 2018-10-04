package com.putable.videx.interfaces;

public interface OIOAbleGlobalMap {
    /**
     * Access the OIOAble that has the given onum, if one exists anywhere in
     * creation
     * 
     * @param onum
     * @return null or the OIOAble
     */
    OIOAble get(int onum);

    /**
     * Create a new instance of a class that implements OIOAble
     * 
     * @param classDescribed
     *            the concrete class to instantiate
     * @param onum
     *            the onum to assign to the created instance, or null to assign
     *            a new onum. onum must not be null between
     *            {@link #startLoading()} and {@link #endLoading()}, and must be
     *            null thereafter.
     * 
     * @return null if classDescribed does not implement OIOAble or the
     *         instantiation fails, otherwise a new instance of the class
     *         described
     */
    <T extends OIOAble> T newOIOAble(Class<?> classDescribed, Integer onum);

    /**
     * Get the onum of oio, creating it if necessary and able.
     * 
     * @param oio
     *            the OIOAble to get the onum of if possible
     * @return the onum (previously or newly) associated with oio
     * @throws IllegalArgumentException
     *             if oio is null
     * @throws IllegalStateException
     *             if oio needs to have an onum assigned but
     *             {@link #endLoading()} has not yet been called
     */
    int getOnum(OIOAble oio);

    void startLoading();

    void endLoading();
}
