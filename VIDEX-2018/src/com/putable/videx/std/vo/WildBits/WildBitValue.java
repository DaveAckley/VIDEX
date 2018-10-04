package com.putable.videx.std.vo.WildBits;

import java.awt.Color;

public enum WildBitValue {
    ZERO   ("0", colorOff( -1,-1,-1), Color.yellow,  "Zero",   "Only 0 is OK here"),
    ONE    ("1", colorOff(  1, 1, 1), Color.yellow,  "One",    "Only 1 is OK here"),
    WILD   ("*", colorOff( -1, 1,-1), Color.yellow,  "Wild",   "Either 0 or 1 is OK here"),
    ILLEGAL("X", Color.orange.darker(), Color.yellow,"Illegal","Neither 0 nor 1 is OK here"),
    UNKNOWN("?", colorOff( -1,-1, 1), Color.yellow,  "Unknown","We don't know if any values are OK here or not"),
    BLANK  (" ", Color.gray, Color.gray,             "Blank",  "This bit doesn't actually exist");
    public final String code;
    public final Color backgroundColor;
    public final Color foregroundColor;
    public final String name;
    private static Color colorOff(int dr, int dg, int db) { 
        final int BASE = 255/3;
        final int SWING = 25;
        return new Color(BASE+dr*SWING,BASE+dg*SWING,BASE+db*SWING);
    }
    private WildBitValue(String code, Color bg, Color fg, String name, String doc) {
        this.name = name;
        this.code = code;
        this.backgroundColor = bg;
        this.foregroundColor = fg;
    }
}
