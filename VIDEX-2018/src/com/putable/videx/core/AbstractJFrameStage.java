package com.putable.videx.core;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.Stage;
import com.putable.videx.interfaces.World;

public abstract class AbstractJFrameStage extends JFrame implements Stage {
    private static final long serialVersionUID = 1L;
    private GraphicsDevice mGraphicsDevice;
    private StagePanel mStagePanel;
    private final Configuration mConfig;
    private final World mWorld;
    private final Hitmap mHitmap;
    
    public Configuration getConfiguration() { return mConfig; }
    public World getWorld() { return mWorld; }
    @Override
    public Hitmap getHitmap() { return mHitmap; }

    @Override
    public SXRandom getRandom() { return mWorld.getRandom(); }
    
    private void initGraphicsDevice() {
        mGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        if (mGraphicsDevice.isFullScreenSupported()) { // Go for full-screen mode
            this.setUndecorated(true);         // Don't show title and border
            //this.setResizable(false);
            //this.setIgnoreRepaint(true);     // Ignore OS re-paint request
            mGraphicsDevice.setFullScreenWindow(this);
        } else {    // Run in windowed mode if full screen is not supported
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(dim.width, dim.height - 40); // minus task bar
            this.setResizable(true);
        }
        
        mStagePanel = new StagePanel(this.mHitmap, this.getRoot(), this.getWidth(), this.getHeight());
        this.setContentPane(mStagePanel); // Set as content pane for this JFrame
    
        // To handle key events
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                dispatchKeyEvent(e);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                dispatchKeyEvent(e);
            }
            @Override
            public void keyTyped(KeyEvent e) {
                dispatchKeyEvent(e);
            }
            
            private void dispatchKeyEvent(KeyEvent e) {
               int keyCode = e.getKeyCode();
              switch (keyCode) {
              case KeyEvent.VK_ESCAPE:   // ESC to quit
                  System.exit(0);
                  break;
              default:
                  mStagePanel.dispatchKeyEvent(e);
                  break;
              }
           }
        });
        this.setFocusable(true);  // To receive key event
    
        // To handle mouse events
        MouseInputAdapter mia = new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("CLICK "+e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("DRAGGED "+e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                System.out.println("ENTERED "+e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                System.out.println("EXITED "+e);
            }
                      
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                System.out.println("MOVED "+e);
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println("PRESSED "+e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("RELEASED "+e);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                super.mouseWheelMoved(e);
                System.out.println("WHEELT "+e);
            }
        };
        this.addMouseListener(mia);
        this.addMouseMotionListener(mia);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle(this.mConfig.getTitle());
        this.pack();            // Pack to preferred size
        this.setVisible(true);  // Show it
    }
    
    @Override
    public void transformStage(World world) {
        this.mStagePanel.transformStage(world);
    }

    @Override
    public void paintStage(World w) {
        repaint();
        this.getToolkit().sync(); // GRRR!
    }

    @Override
    public void initStage() {
        initVOs();
        initGraphicsDevice();
    }

    public AbstractJFrameStage(World world, Configuration config) {
        this.mWorld= world;
        this.mConfig = config;
        this.mHitmap = new Hitmap(mWorld.getRandom());
    }


    @Override
    public void destroyStage() {
        mGraphicsDevice.setFullScreenWindow(null);
    }

    abstract public void initVOs() ;
}
