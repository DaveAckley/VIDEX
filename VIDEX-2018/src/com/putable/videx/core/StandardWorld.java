package com.putable.videx.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import javax.swing.Timer;

import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.Universe;
import com.putable.videx.interfaces.World;

public abstract class StandardWorld implements World, ActionListener {
    private int mFramesPerSecondTarget;
    private boolean mThreadRunning;
    private Timer mTimer;
    private final String mWorldName;
    private Universe mUniverseOrNull;
    
    private List<Stage> mStages = new LinkedList<Stage>();
    private SXRandom mRandom = new SXRandom();
    private final Configuration mConfig;
    
    @Override
    public String getName() {
        return mWorldName;
    }
    
    @Override
    public SXRandom getRandom() {
        return mRandom;
    }
    @Override
    public void destroyWorld() {
        for (Stage s : mStages) s.destroyStage();
    }

    @Override
    public boolean addStage(Stage stage) {
        if (!mStages.contains(stage)) {
            mStages.add(stage);
            return true;
        }
        return false;
    }

    @Override
    public Stage removeStage(Stage stage) {
        if (mStages.contains(stage)) {
            mStages.remove(stage);
            return stage;
        }
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return mConfig;
    }
    
    public StandardWorld(Configuration config) {
        this.mWorldName = config.getWorldName();
        this.mConfig = config;
        this.mFramesPerSecondTarget = config.getFPS();
        if (this.mFramesPerSecondTarget < 0) this.mFramesPerSecondTarget = 0;
    }

    @Override
    public void forEach(Consumer<? super Stage> arg0) {
        mStages.forEach(arg0);
    }

    @Override
    public Iterator<Stage> iterator() {
        return mStages.iterator();
    }

    @Override
    public Spliterator<Stage> spliterator() {
        return mStages.spliterator();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (mThreadRunning) {
            for (Stage s : this) s.transformStage(this);
            for (Stage s : this) s.paintStage(this);
            for (Stage s : this) s.updateStage(this);  
        } else {
            throw new RuntimeException("HUH NOW WOT?");
        }
    }

    @Override
    public Universe getUniverseOrNull() {
        return mUniverseOrNull;
    }
    
    @Override
    public void runWorld(Universe u) {
        mUniverseOrNull = u;
        mThreadRunning = true;
        int msDelay = 10;
        if (StandardWorld.this.mFramesPerSecondTarget > 0) {
            msDelay = 1000 / StandardWorld.this.mFramesPerSecondTarget;
        }
        mTimer = new Timer(msDelay,this);
        mTimer.setInitialDelay(250);
        //mTimer.setDelay(msDelay);
        //mTimer.setCoalesce(false);
        mTimer.start();
        System.out.println("TIMER STARTED");
        while (mThreadRunning) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
