package com.putable.videx.drivers;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import com.putable.videx.core.AbstractConfiguration;
import com.putable.videx.core.AbstractJFrameStage;
import com.putable.videx.core.StagePanel;
import com.putable.videx.core.StandardWorld;
import com.putable.videx.core.oio.OIOException;
import com.putable.videx.core.oio.load.GlobalOnumMap;
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.vo.StageVO;

public class OIOLoadConfiguration extends AbstractConfiguration {
    private final GlobalOnumMap mOmap = new GlobalOnumMap();
    private final OIOLoad mLoader;
    private final boolean mWantFullScreen;
    private final Point2D mStagePanelScale;
    public OIOLoad getLoader() {
        return mLoader;
    }

    @Override
    public boolean wantFullScreen() { return mWantFullScreen; }

    @Override
    public Point2D getStagePanelScale() { return mStagePanelScale; }
    public OIOLoadConfiguration(String basedir, String worldname, boolean fullscreen, Point2D scale, Rectangle2D window) {
        super(worldname,window);
        mWantFullScreen = fullscreen;
        mStagePanelScale = scale;
        mLoader = new OIOLoad(basedir, mOmap, worldname);
    }
    
    private static class OIOLoadWorld extends StandardWorld {
        public OIOLoadWorld(OIOLoadConfiguration conf) { 
            super(conf);
            Stage s = new OIOLoadStage(this,conf);
            s.initStage();
            this.addStage(s);
        }
    }

    private static class OIOLoadStage extends AbstractJFrameStage {
        private final OIOLoadConfiguration mConfig;
        public VO mLoadedRoot = null;

        private boolean reloadIfNeeded() {
            try {
                return reloadIfNeededInternal();
            } catch (IOException | OIOException e) {
                e.printStackTrace();
                System.err.println("RELOAD FAILED");
                System.exit(101);
            }
            // BUT THIS IS UNREACHABLE CHUMLEY
            return true;
        }
        
        private boolean reloadIfNeededInternal() throws IOException, OIOException {
            OIOAble top = mConfig.mLoader.loadIfNeeded();
            if (top == null) return false; // Not needed
            if (!(top instanceof VO))
                throw new OIOException("Not a VO "+top);
            if (mLoadedRoot != null)
                mLoadedRoot.killVO();
            mLoadedRoot = (VO) top;
            mRoot.addPendingChild(mLoadedRoot);
            return true;
        }

        public OIOLoadConfiguration getConfiguration() {
            return mConfig;
        }
        
        public OIOLoadStage(World world, OIOLoadConfiguration config) {
            super(world, config);
            mConfig = config;
            mRoot = new StageVO(world);
        }

        private static final long serialVersionUID = 1L;

        private StageVO mRoot;
        
        @Override
        public Stage.Purpose getPurpose() { return Stage.Purpose.MAIN_SCREEN; }

        @Override
        public void updateStage(World world) {
            if (!reloadIfNeeded()) {
                StagePanel sp = this.getStagePanel();
                if (sp != null)
                    sp.setPanelScale(this.getConfiguration().getStagePanelScale());
                mRoot.updateVO(this);
            }
        }

        
        @Override
        public void initVOs() {
            reloadIfNeeded();
        }

        @Override
        public StageVO getRoot() {
            return mRoot;
        }

        @Override
        public VO mapPixelToVOC(Point2D in, Point2D out) {
            throw new UnsupportedOperationException("NOT CALLED RITE");
        }
    }

    @Override
    public World buildWorld(Configuration config) {
        return new OIOLoadWorld(this);
    }

    @Override
    public String getTitle() {
        return MY_CLASS.getName();
    }

    @Override
    public int getFPS() {
        return 30;
    }

    public static final Class<?> MY_CLASS = MethodHandles.lookup().lookupClass();

    /*
    public static void main(String[] s) {
        CompilerDriver.main(new String[] { s[0], MY_CLASS.getName() });
    }
    */
}
