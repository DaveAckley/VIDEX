package com.putable.videx.std.vo;


import com.putable.videx.core.Pose;
import com.putable.videx.core.SXRandom;
import com.putable.videx.core.VOGraphics2D;
import com.putable.videx.core.oio.OIO;
import com.putable.videx.interfaces.Stage;

public class WiggleSquare extends Square {
    @OIO
    private float mScaleScale = 0.1f;
    @OIO
    private float mScale = 1f;
    @OIO
    private float mXvel = 0f;
    @OIO
    private float mYvel = 0f;
    @OIO
    private float mSvel = 1f;
    @OIO
    private float mRvel = 0f;

    @Override
    public boolean updateThisVO(Stage stage) {
        SXRandom r = stage.getRandom();
        Pose p = this.getPose();
        this.getPose().setOAX(this.getPose().getOAX() + r.around(0, .5));
        this.getPose().setOAY(this.getPose().getOAY() + r.around(0, .5));
        mXvel += mScale * r.around(0.0, .5);
        mYvel += mScale * r.around(0.0, .5);
        mSvel *= (100f - mScaleScale + 2f * mScaleScale * r.nextFloat()) / 100f;
        mRvel += mScale / 50f * (r.nextFloat() - 0.5f);
        // p.setX(p.getX() + mXvel);
        // p.setY(p.getY() + mYvel);
        // p.setS(p.getS() * mSvel);
        p.setR(p.getR() + mRvel);
        return true;
    }

    @Override
    public void drawThisVO(VOGraphics2D v2d) {
        super.drawThisVO(v2d);
        double rx = getPose().getOAX();
        double ry = getPose().getOAY();
        v2d.getGraphics2D().clearRect((int) (rx - 1), (int) (ry - 1), (3), (3));
    }

    public WiggleSquare() {
        this(0, 0);
    }

    public WiggleSquare(int x, int y) {
        this.getPose().setPAX(x);
        this.getPose().setPAY(y);
        this.getPose().setOAX(50);
        this.getPose().setOAY(25);
    }

}
