package com.putable.videx.core.serdes;

public class ParseException extends RuntimeException {
    public final Token onToken;
    public final String message;
    public ParseException(Token onToken, String message) {
        this.onToken = onToken;
        this.message = message;
    }
    public String toString() {
        return onToken + ": " + message;
    }
}
