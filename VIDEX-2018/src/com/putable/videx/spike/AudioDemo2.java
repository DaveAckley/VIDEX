package com.putable.videx.spike;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioDemo2 {
	
	public static void play(String filename)
	{
		try
		{
			File initialFile = new File(filename);
			AudioInputStream audioInputStream =	AudioSystem.getAudioInputStream(initialFile);
			Clip clip = AudioSystem.getClip();
			
			clip.open(audioInputStream);
			clip.start();
			Thread.sleep(500);
			clip.stop();
			clip.setFramePosition(0);
			clip.start();
			Thread.sleep(3000);
		}
		catch (Exception exc)
		{		
			exc.printStackTrace(System.out);
		}
	}

	public static void main(String[] args) {
		play(args[0]);
	}

}

