package com.putable.videx.core;

import java.awt.geom.Rectangle2D;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.putable.videx.utils.DevNullConsumer;

public class VLCMoviePlayer {
    private Path mMoviePath;
    private Rectangle2D mWhere;
    public VLCMoviePlayer() {
    }
    private static final String[] cSTD_CMD_ARGS = {
            "vlc",
            "--no-loop",
            "--play-and-exit",
            "--input-repeat","0",
            "--mouse-hide-timeout","100",
            "--video-on-top",
            "--novideo-title-show",
            "--fullscreen"
    };
    public void play(Path moviePath, Rectangle2D at) {
        mMoviePath = moviePath;
        mWhere = at;
        List<String> l = new ArrayList<String>();
        l.addAll(Arrays.asList(cSTD_CMD_ARGS));
        l.add(moviePath.toString());
        String[] opargs = l.toArray(cSTD_CMD_ARGS);
        new ExternalProgramRunner(opargs,
                new DevNullConsumer(),
                new DevNullConsumer());
    }
    public static void main(String[] args) {
        VLCMoviePlayer vlc = new VLCMoviePlayer();
        vlc.play(Paths.get("/data/ackley/AV/CachedVideos/npd.mp4"), null);
    }
}
