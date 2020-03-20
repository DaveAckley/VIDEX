package com.putable.videx.utils;

import java.util.function.Consumer;

public class StringConsumer implements Consumer<String> {
    private StringBuilder mString = new StringBuilder();
    @Override
    public void accept(String t) {
        mString.append(t);
        mString.append("\n");
    }
    public String toString() { return mString.toString(); }
}