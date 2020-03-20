package com.putable.videx.std.riders;

import java.nio.file.Paths;

import com.putable.videx.core.VLCMoviePlayer;
import com.putable.videx.core.oio.OIO;

public class PlayFullscreenMovieRider extends TogglePresentationRider {

    @OIO
    private String mMoviePath;

    protected void togglePresentation() {
        if (!isPresented()) {
            System.out.println("LAUNCH "+mMoviePath);
            if (mMoviePath != null) {
                VLCMoviePlayer vlc = new VLCMoviePlayer();
                Thread playIt = new Thread() {
                    public void run() {
                        vlc.play(Paths.get(mMoviePath), null);
                    }
                };
                playIt.start();
            }
        }
        super.togglePresentation();
    }

}
