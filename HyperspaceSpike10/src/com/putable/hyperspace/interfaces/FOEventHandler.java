package com.putable.hyperspace.interfaces;

import com.putable.hyperspace.core.FOEvent;
import com.putable.hyperspace.core.FOEventClick;
import com.putable.hyperspace.core.FOEventKey;

public interface FOEventHandler {
	public default boolean handle(FOEvent foe) {
		if (foe instanceof FOEventClick) {
			return handleClick((FOEventClick) foe);
		}
		if (foe instanceof FOEventKey) {
			return handleKey((FOEventKey) foe);
		}
		return false;
	}
	public default boolean handleClick(FOEventClick foec) {
		return false;
	}
	public default boolean handleKey(FOEventKey foek) {
		return false;
	}
	
	
}
