package com.putable.videx.utils;

import java.util.function.Consumer;

public class DevNullConsumer implements Consumer<String> {
    @Override
    public void accept(String t) {
        /*bye now*/
    }
}