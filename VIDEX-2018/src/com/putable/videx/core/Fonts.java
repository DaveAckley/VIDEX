package com.putable.videx.core;

import java.awt.Font;
import java.util.HashMap;

public class Fonts {
    public static final Fonts SINGLETON = new Fonts();

    private HashMap<String, Font> mLoadedFonts = new HashMap<String, Font>();

    public String getFontKey(String name, int style, int size) {
        return String.format("%s|%04x@%d", name, style, size);
    }

    public Font getFont(String name, int style, int size) {
        String key = getFontKey(name,style,size);
        Font f = mLoadedFonts.get(key);
        if (f == null) {
            f = new Font(name, style, size);
            mLoadedFonts.put(key, f);
        }
        return f;
    }
}
