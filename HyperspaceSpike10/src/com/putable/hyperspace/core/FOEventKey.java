package com.putable.hyperspace.core;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

public class FOEventKey extends FOEvent {
	public final KeyEvent mKeyEvent;
	public final Point2D mKeyAt;
	public FOEventKey(KeyEvent ke, Point2D at) { 
		mKeyEvent = ke;
		mKeyAt = new Point2D.Double(at.getX(), at.getY());
	}
	public Character keyTyped(char... opts) {
		if (mKeyEvent.getID()==KeyEvent.KEY_TYPED) 
			for (char ch : opts) 
				if (mKeyEvent.getKeyChar() == ch) return Character.valueOf(ch);
		return null;
	}
}
