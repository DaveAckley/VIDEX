package com.putable.videx.core;

import java.awt.geom.Point2D;

public class Int2D {
    private int x;
    private int y;

    public Point2D asFloat() {
        return new Point2D.Float(x, y);
    }

    public Point2D asDouble() {
        return new Point2D.Double(x, y);
    }

    public Int2D() {
        this(0, 0);
    }

    public Int2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Int2D(Point2D p) {
        this((int) p.getX(), (int) p.getY());
    }

    public Int2D(Int2D i) {
        this(i.x, i.y);
    }

    public int euclideanDistanceSquared(Int2D other) {
        return (x - other.x)*(x - other.x) + (y - other.y)*(y - other.y);
    }

    public int manhattanDistance(Int2D other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
    
    public Int2D subtract(Int2D minus, Int2D result) {
        if (result == null) result = new Int2D();
        result.set(this);
        result.x -= minus.x;
        result.y -= minus.y;
        return result;
    }

    public Int2D add(Int2D plus, Int2D result) {
        if (result == null) result = new Int2D();
        result.set(this);
        result.x += plus.x;
        result.y += plus.y;
        return result;
    }

    public Int2D multiply(int value, Int2D result) {
        if (result == null) result = new Int2D();
        result.set(this);
        result.x *= value;
        result.y *= value;
        return result;
    }
    public Int2D multiply(double dvalue, Int2D result) {
        if (result == null) result = new Int2D();
        result.set(this);
        result.x = (int) (x*dvalue);
        result.y = (int) (y*dvalue);
        return result;
    }
    
    public void set(Point2D p) {
        set((int) p.getX(), (int) p.getY());
    }

    public void set(Int2D p) {
        set(p.getX(), p.getY());
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Int2D(" + x + ", " + y + ")";
    }

    public int hash() {
        return (x ^ y) + (x << 3) + (y << 5);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Int2D)) return false;
        Int2D o = (Int2D) other;
        return x == o.x && y == o.y;
    }
}
