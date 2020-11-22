package com.putable.hyperspace.interfaces;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface Body {
	public Color getFg() ;
	public Color getBg() ;
	public void setFg(Color col) ;
	public void setBg(Color col) ;
	public Rectangle2D.Double getBounds2D() ;
	public void setBounds(Rectangle2D rect) ;

	public default void copy(Body b) {
		this.setBg(b.getBg());
		this.setFg(b.getFg());
		this.setBounds(b.getBounds2D());
	}
	public default double getX() { return getBounds2D().getX(); }
	public default double getY() { return getBounds2D().getY(); }
	public default double getWidth() { return getBounds2D().getWidth(); }
	public default double getHeight() { return getBounds2D().getHeight(); }
	public default double getCenterX() { return getBounds2D().getCenterX(); }
	public default double getCenterY() { return getBounds2D().getCenterY(); }
	public default Point2D getCenter(Point2D ptornull) { 
		if (ptornull == null) ptornull = new Point2D.Double();
		ptornull.setLocation(getCenterX(), getCenterY());
		return ptornull;
	}

	public default boolean equals(Body other) {
		return this.getBg().equals(other.getBg())
				&& this.getFg().equals(other.getFg())
				&& this.getBounds2D().equals(other.getBounds2D());
	}

	public default void setX(double x) {
		this.getBounds2D().x = x;
	}
	public default void setY(double y) {
		this.getBounds2D().y = y;
	}
	public default void setWidth(double w) {
		this.getBounds2D().width = w;
	}
	public default void setHeight(double h) {
		this.getBounds2D().height = h;
	}
}
