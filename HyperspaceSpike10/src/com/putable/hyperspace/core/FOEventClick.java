package com.putable.hyperspace.core;

import java.awt.geom.Point2D;

public class FOEventClick extends FOEvent {
	public final int mButton;
	public final Point2D mPoint;
	public FOEventClick(int button, Point2D pt) {
		mButton = button;
		mPoint = pt;
	}
}
