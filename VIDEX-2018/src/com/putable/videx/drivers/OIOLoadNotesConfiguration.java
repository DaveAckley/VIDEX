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
import com.putable.videx.core.oio.save.OIOLoad;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.OIOAble;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.vo.StageVO;

public class OIOLoadNotesConfiguration extends AbstractConfiguration {
    private final OIOLoad mLoader;

    public OIOLoad getLoader() {
        return mLoader;
    }
    
    public OIOLoadNotesConfiguration(OIOLoad oio) {
        super("OBSOLETE",new Rectangle2D.Double(0,0,100,100));
        throw new IllegalStateException();
    }
    
    private static class OIONotesWorld extends StandardWorld {
        public OIONotesWorld(OIOLoadNotesConfiguration conf) { 
            super(conf);
            Stage s = new OIONotesStage(this,conf);
            s.initStage();
            this.addStage(s);
        }
    }
    private static class OIONotesStage extends AbstractJFrameStage {
        private final OIOLoadNotesConfiguration mConfig;
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

        public OIOLoadNotesConfiguration getConfiguration() {
            return mConfig;
        }
        
        public OIONotesStage(World world, OIOLoadNotesConfiguration config) {
            super(world, config);
            mConfig = config;
            mRoot = new StageVO(world);
        }

        private static final long serialVersionUID = 1L;

        private StageVO mRoot;

        @Override
        public Stage.Purpose getPurpose() { return Stage.Purpose.PRESENTER_SCREEN; }

        @Override
        public void updateStage(World world) {
            if (!reloadIfNeeded()) {
                StagePanel sp = this.getStagePanel();
                if (sp != null)
                    sp.setPanelScale(new Point2D.Double(1,-1));

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
        throw new IllegalStateException();
        //return new OIOLoadWorld(this);
    }

    @Override
    public String getTitle() {
        return MY_CLASS.getName();
    }

    @Override
    public int getFPS() {
        return 10;
    }

    public static final Class<?> MY_CLASS = MethodHandles.lookup().lookupClass();

    @Override
    public boolean wantFullScreen() {
        return false;
    }

    @Override
    public Point2D getStagePanelScale() {
        throw new IllegalStateException();
    }

   
    /*
    public static void main(String[] s) {
        CompilerDriver.main(new String[] { s[0], MY_CLASS.getName() });
    }
    */
}
