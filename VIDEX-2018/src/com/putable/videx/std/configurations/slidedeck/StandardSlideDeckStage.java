package com.putable.videx.std.configurations.slidedeck;

import java.awt.geom.Point2D;

import com.putable.videx.core.AbstractJFrameStage;
import com.putable.videx.interfaces.Configuration;
import com.putable.videx.interfaces.VO;
import com.putable.videx.interfaces.World;
import com.putable.videx.std.vo.StageVO;

public class StandardSlideDeckStage extends AbstractJFrameStage {

    public StandardSlideDeckStage(World world, Configuration config) {
        super(world, config);
    }

    private static final long serialVersionUID = 1L;

    private StageVO mRoot = new StageVO();

    private void loadSlideDeck() {
        String[] args = this.getConfiguration().theArguments(null);
        if (args == null || args.length != 1)
            throw new IllegalStateException(this.getClass().getName() + ": Need SlideDeck directory argument");
        String dir = args[0];
        throw new UnsupportedOperationException("DEIMPLEMENTED poopab");
        //OIOCompiler compiler = new OIOCompiler(dir, null);
        //compiler.reload();
        //ASTNode tree = compiler.getSlideDeck();
        //throw new UnsupportedOperationException("poopab");
    }
    
    @Override
    public void updateStage(World world) {
        mRoot.updateVO(this);
    }

    @Override
    public void initVOs() {
        loadSlideDeck();
/*
        StandardVO vo, vo1;
        VO blackVO = new BlackBackground();
        mRoot.addPendingChild(blackVO);
        WildBitVector firstAutoVec;
        {
            firstAutoVec = new WildBitVector(32) {
                int val = 0, mask = 0;
                @Override
                public boolean updateThisVO(Stage stage) {
                    SXRandom r = stage.getRandom(); 
                    if (r.oneIn(5)) {
                        int bit = r.create(32);
                        int m = 1<<bit;
                        getAt(bit).setLabelPosted(false);
                        getAt(bit).setValueWritten(true);
                        if (r.oneIn(2)) val ^= m; 
                        if (r.oneIn(2)) mask |= m; 
                        this.setWildBits(val, mask);
                    }
                    return true;
                }
            };
            for (int i = 0; i < firstAutoVec.getLength(); ++i) {
                firstAutoVec.getAt(i).setLabel("I am possibly bit #" + i);
            }
            firstAutoVec.getPose().setPA(new Point2D.Float(2000, 500));
            firstAutoVec.getPose().setS(5);
            blackVO.addPendingChild(firstAutoVec);
        }
        {
            WildBitVectorOp u = new VOBitOpUNION();
            u.getPose().setPAX(2222);
            u.getPose().setPAY(1111);
            u.getPose().setS(5);
            blackVO.addPendingChild(u);

            WildBitVector secondVO = new WildBitVector(32);
            secondVO.setBits(0xfeedf00d);
            secondVO.getPose().setBasic(1500, 1550, 5);
            blackVO.addPendingChild(secondVO);
            WildBitVector outVO = new WildBitVector(32);
            outVO.getPose().setBasic(1500, 1950, 5);
            blackVO.addPendingChild(outVO);
            
            u.addOutput(outVO);
            u.addInput(firstAutoVec);
            u.addInput(secondVO);
            
        }
        blackVO.addPendingChild(vo1 = new UnitAxes(300, 250, 1,
                (float) (0 * Math.PI / 180)));
        blackVO.addPendingChild(vo = new UnitAxes(1050, 850, 9.1f,
                (float) (45 * Math.PI / 180)));
        blackVO.addPendingChild(new ConnectorLine(vo1, vo, true, true));
        vo.addPendingChild(vo = new LabeledPoint(50, 50));
        vo.setForeground(Color.red);
        blackVO.addPendingChild(vo = new UnitAxes(800, 250, 5.5f,
                (float) (0 * Math.PI / 180)));
        vo.addPendingChild(vo = new LabeledPoint(20, 30));
        vo.setForeground(Color.green);
        blackVO.addPendingChild(new UnitAxes(300, 500, 1,
                (float) (90 * Math.PI / 180)));
        blackVO.addPendingChild(vo = new UnitAxes(550, 500, 0.9f,
                (float) (90 * Math.PI / 180)));
        vo.addPendingChild(vo = new LabeledPoint(10, 20));
        vo.setForeground(Color.yellow);
        blackVO.addPendingChild(new UnitAxes(300, 750, 1,
                (float) (180 * Math.PI / 180)));
        blackVO.addPendingChild(new UnitAxes(300, 1000, 1,
                (float) (270 * Math.PI / 180)));
        blackVO.addPendingChild(new WiggleSquare(1800, 1500));
        blackVO.addPendingChild(new Square());
        
        final String TEST_IMAGE = "/data/ackley/2018-ECLIPSE-REDO/VIDEX-2018/images/bigjim1.png";
        blackVO.addPendingChild(new Image(TEST_IMAGE));
        
        FileSlide ss = new FileSlide(new File("/home/ackley/SLIDEDECK/text0.html"));
        //ss.setText("<html>Hi<br><b>HOHO</b><i>wangg</i></br><ul><li>foo</li><li>Bar</li></ul>BYE!");
        blackVO.addPendingChild(ss);
        
        SlideDeck sd = new SlideDeck("/home/ackley/SLIDEDECK");
        blackVO.addPendingChild(sd);
  */
    }

    @Override
    public StageVO getRoot() {
        return mRoot;
    }

    @Override
    public VO mapPixelToVOC(Point2D in, Point2D out) {
        // TODO Auto-generated method stub
        return null;
    }

}
