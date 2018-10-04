package com.putable.videx.interfaces;

/**
 * A Driver is associated with some VO and can observe and make changes to that
 * VO's state (and perhaps its children or surrounding state). Drivers are run
 * during StandardVO.updateVO
 * 
 * @author ackley
 * 
 */
public interface Driver extends OIOAble {
    /**
     * Reaction meaning everything is as expected, continue processing normally.
     */
    public static final int REACT_CONTINUE = 0;

    /**
     * Reaction meaning we should completely start over with {@link #awaken()}
     * followed by {@link #observe(VO)} right now and then act as if
     * {@link #REACT_CONTINUE}.
     */
    public static final int REACT_REAWAKEN = 1;

    /**
     * Reaction meaning we do {@link #observe(VO)} right now and then act as if
     * {@link #REACT_CONTINUE}.
     */
    public static final int REACT_REOBSERVE = 2;

    /**
     * Reaction meaning things weren't as expected, but we're going to ignore
     * those differences and act like {@link #REACT_CONTINUE} anyway.
     */
    public static final int REACT_REASSERT = 3;

    /**
     * Reaction meaning this Driver's job is now done. No further processing
     * should be done now, and this Driver should be deleted when possible.
     */
    public static final int REACT_DIE = 4;

    /**
     * Reaction meaning this Driver is done with this VO (though perhaps not
     * done in general). This Driver does not act now, and it should be removed
     * from this VO and not considered further.
     */
    public static final int REACT_REMOVE = 5;
    /**
     * Reaction meaning this Driver's driven VO is now done. No further
     * processing should be done via this driver or any other, and the driven VO
     * should be killed (and reaped as usual.)
     */
    public static final int REACT_KILL = 6;

    /**
     * Perform any start-up internal initialization this Driver needs.
     */
    void awaken();

    /**
     * Capture any relevant state from vo
     * 
     * @param vo
     *            The VO this Driver is now driving
     */
    void observe(VO vo);

    /**
     * Compare state from this vo to that previously captured by
     * {@link #observe(VO)}, and determine how to react.
     * 
     * @param vo
     *            The VO this Driver is now driving. Note that in the general
     *            case this might be a different VO than was last observed!
     * @return a code indicating what to do, one of {@link #REACT_CONTINUE},
     *         {@link #REACT_REAWAKEN}, {@link #REACT_REOBSERVE},
     *         {@link #REACT_REASSERT}, {@link #REACT_DIE}.
     */
    int react(VO vo);

    /**
     * Update
     */
    void act();

}
