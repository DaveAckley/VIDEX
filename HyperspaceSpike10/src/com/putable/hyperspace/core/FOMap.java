package com.putable.hyperspace.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class FOMap {
	private int mAFOHitmapNextIndex = 0x000002;
	private final int AFO_MAP_INCREMENT = 0x1234;
	private Map<Integer,FO> mGlobalFOMap = new LinkedHashMap<Integer,FO>();
	private int getIndexForFO(FO fo) {
		if (fo.getIndex() != 0) 
			throw new IllegalStateException();
		int idx = mAFOHitmapNextIndex;
		mAFOHitmapNextIndex+=AFO_MAP_INCREMENT;
		if (mAFOHitmapNextIndex > 0x00ffffff)
			throw new ArrayIndexOutOfBoundsException();
		mGlobalFOMap.put(idx, fo);
		return idx;
	}
	public FO getFOForIndexIfAny(int index) {
		return mGlobalFOMap.get(index);
	}
}
