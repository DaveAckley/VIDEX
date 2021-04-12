package com.putable.videx.std.vo.Slides;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.putable.videx.core.Pose;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.events.KeyboardEventInfo;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.std.specialevents.RunGenericSpecialEventInfo;
import com.putable.videx.std.vo.PopupTextLineEntry;
import com.putable.videx.std.vo.TimedNotification;
import com.putable.videx.utils.FileUtils;

public class BasicSlideAudioClip extends BasicSlide {
    @OIO
	private String mWavPath = null;
    
    @OIO
    private String mPlayKey = null; 

    private boolean startClipIfAny() {
    	if (mClip == null) return false;
    	if (mClip.isRunning()) mClip.stop();
    	mClip.setFramePosition(0);
    	mClip.start();
    	return true;
    }
    private Clip mClip = null;
    private boolean initFromWAVDataIfAvailable() {
        if (mWavPath == null) return false;
        if (mClip != null) return true;
        File f = new File(mWavPath);
        AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(f);
	        mClip = AudioSystem.getClip();
	        mClip.open(ais);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
    }

    public boolean isClipPlaying() {
    	if (mClip == null) return false;
    	return mClip.isActive();
    }
    
    @Override
	public boolean updateThisVO(Stage stage) {
    	initFromWAVDataIfAvailable();
    	return super.updateThisVO(stage);
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
    	if (isClipPlaying())
    		super.drawThisVO(v2d);
    }

    @Override
    public boolean handleKeyboardEventHere(KeyboardEventInfo kei) {
    	KeyEvent ke = kei.getKeyEvent();
    	System.out.println("BasicSlideAudioClip hKEH "+ke.getKeyChar() + kei);

    	if (ke.getID() == KeyEvent.KEY_TYPED) {
    		char ch = ke.getKeyChar();
    		if (Character.toString(ch).equals(mPlayKey)) {
    			return startClipIfAny();
    		}
        }
        return false;
    }

}
