package com.putable.videx.std.riders;

import java.awt.geom.Point2D;

import com.putable.videx.core.EventAwareVO;
import com.putable.videx.core.Pose;
import com.putable.videx.core.StandardVO;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.events.SpecialEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.VO;
import com.putable.videx.std.specialevents.RunGenericSpecialEventInfo;
import com.putable.videx.std.vo.TimedNotification;

/**
 * ZoomOutRider is designed as part of the T2sday Update opening sequence. Its
 * effect should be that, before triggering, its mount is invisible. Upon
 * triggering, the mount appears, very large, on top of 'everything else'. After
 * triggering, the mount zooms out rapidly (via SOSIPose) towards a configured
 * destination (tiny, vanishing-point-ish) Pose. Upon reaching the desintation,
 * the mount disappears, and resets (invisibly) to its first position, as does
 * the ZoomOutRider.
 * 
 * In addition, at a configurable point in its zoom-out, the ZoomOutRider emits
 * some kind of special event that will trigger the ZoomOutRider, if any, on the
 * nearest downstream mount.
 * 
 * @author ackley
 *
 */
public class ZoomOutRider extends SOSIPoseRider {

    {
        this.setDieOnComplete(false);
    }
    
    @OIO
    private boolean mHoldAtEnd = false;
    
    @OIO
    private boolean mSequenceLeader = false;

    private void toggleLeader(VO on) {
        mSequenceLeader = !mSequenceLeader;
        String state = "SequenceLeader: " + mSequenceLeader;
        TimedNotification.postOn(on, state);
    }
    @OIO
    private boolean mRiderActive = false;

    private static final double DEFAULT_START_SCALE = 4.0;
    private static final double DEFAULT_END_SCALE = .05;

    @OIO
    private Pose mStartPose = Pose.make(new Point2D.Double(1000, 2000), 0.0,
            new Point2D.Double(DEFAULT_START_SCALE, DEFAULT_START_SCALE),
            new Point2D.Double(0, 0));
    @OIO
    private Pose mEndPose = Pose.make(new Point2D.Double(1000, 2000), 0.0,
            new Point2D.Double(DEFAULT_END_SCALE, DEFAULT_END_SCALE),
            new Point2D.Double(0, 0));

    {
        // protected in SOSIPoseRider
        this.mMaxStep = 20;
    }

    @OIO
    private int mStepsToTriggerNext = 5;

    public static final int STATE_RESET = -1; // Pending reinit
    public static final int STATE_FIRST_POSITION = 0; // Not yet triggered
    public static final int STATE_STARTING = 1; // Pre downstream trigger
    public static final int STATE_FINISHING = 2; // Post downstream trigger
    public static final int STATE_FINAL_POSITION = 3; // Just before resetting

    @OIO
    private int mCurrentState = STATE_RESET;

    private void gotoFirstPosition() {
        this.copyTo(this.mStartPose);
        this.copyFrom(this.mStartPose);
        this.setCurrentStep(mMaxStep); // do not pass go?
        mCurrentState = STATE_FIRST_POSITION;
        StandardVO veh = this.getLastStandardVehicle();
        if (veh != null)
            veh.setEnabled(false);
    }

    private void gotoFinishing() {
        triggerNext();
        mCurrentState = STATE_FINISHING;
    }

    private void gotoFinal() {
        mCurrentState = STATE_FINAL_POSITION;
    }

    @OIO(owned = false)
    private ZoomOutRider mNextRider = null;

    private void triggerNext() {
        if (mNextRider != null)
            mNextRider.triggerStart();
    }

    private void triggerStart() {
        this.copyTo(this.mEndPose);
        this.copyFrom(this.mStartPose);
        this.setCurrentStep(0);
        this.mCurrentState = STATE_STARTING;
        StandardVO veh = this.getLastStandardVehicle();
        if (veh != null) // hmm
            veh.requestTop(); // may have display sequencing issue here..
        if (veh != null)
            veh.setEnabled(true);
    }

    private boolean updateState() {
        switch (mCurrentState) {
        default:
        case STATE_FINAL_POSITION:
            if (!mHoldAtEnd)
                gotoFirstPosition();
            return true;
        case STATE_FIRST_POSITION:
            return true;
        case STATE_STARTING:
            if (this.getCurrentStep() >= mStepsToTriggerNext)
                gotoFinishing();
            return true;
        case STATE_FINISHING:
            if (this.getCurrentStep() >= mMaxStep)
                gotoFinal();
            return true;
        }
    }

    @Override
    public void act() {
        if (!this.mRiderActive)
            return;
        if (updateState())
            super.act();
    }

    @Override
    public boolean handleSpecialEventHere(SpecialEventInfo sei) {
        if (sei instanceof RunGenericSpecialEventInfo && mSequenceLeader) {
            triggerStart();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
        VO veh = this.getVehicle();
        if (veh instanceof EventAwareVO) {
            EventAwareVO evo = (EventAwareVO) veh;
            if (!evo.isMouseTarget())
                return false;
        }
        if (kei.isKeyTyped('K')) {
            if (veh != null)
                veh.killVO();
            return true;
        }
        if (kei.isKeyTyped('s')) { // Toggle sequence leader
            toggleLeader(veh);
            return true;
        }
        
        if (kei.isKeyTyped('R')) { // Run
            triggerStart();
            return true;
        }
        if (kei.isKeyTyped('D')) { // Disable veh
            if (veh != null) veh.setEnabled(false);
            return true;
        }
        if (kei.isKeyTyped('a')) {
            this.mRiderActive = !this.mRiderActive;
            if (veh != null) TimedNotification.postOn(veh, "RiderActive: "+this.mRiderActive);
            return true;
        }
        if (kei.isKeyTyped('b')) { // set begin position
            if (veh != null) {
                mStartPose.copy(veh.getPose());
                TimedNotification.postOn(veh, "Set zoom begin: "+this.mStartPose);
            }
            return true;
        }
        if (kei.isKeyTyped('B')) { // goto begin position
            if (veh != null) {
                veh.getPose().copy(mStartPose);
                TimedNotification.postOn(veh, "Current begin: "+this.mStartPose);
            }
            return true;
        }
        if (kei.isKeyTyped('e')) { // set end position
            if (veh != null) {
                mEndPose.copy(veh.getPose());
                TimedNotification.postOn(veh, "Set zoom end: "+this.mEndPose);
            }
            return true;
        }
        if (kei.isKeyTyped('E')) { // goto end position
            if (veh != null) {
                veh.getPose().copy(mEndPose);
                TimedNotification.postOn(veh, "Current zoom end: "+this.mEndPose);
            }
            return true;
        }
        return false;
    }

}
