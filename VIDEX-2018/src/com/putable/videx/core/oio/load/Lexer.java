package com.putable.videx.core.oio.load;

import java.io.Reader;
import java.io.StreamTokenizer;

public class Lexer {
    private String mFileName;
    private StreamTokenizer mTokenizer;
    private Token mToken = null;
    private boolean mPushedBack = false;
    private Reader mReader;

    public Lexer() { }

    public Lexer(String filename, Reader reader) {
        this();
        setReader(filename, reader);
    }

    public void setReader(String filename, Reader reader) {
        mFileName = filename;
        mReader = reader;
        mTokenizer = new StreamTokenizer(mReader);
        mTokenizer.lowerCaseMode(false);
        mTokenizer.eolIsSignificant(false);
        mTokenizer.slashSlashComments(true);
        mTokenizer.slashStarComments(true);
    }

    public Token nextToken() {
        if (mPushedBack) {
            mPushedBack = false;
            return mToken;
        }
        return mToken = new Token(mFileName, mTokenizer);
    }

    public void pushBack() {
        if (mPushedBack)
            throw new IllegalStateException();
        mPushedBack = true;
    }

    public Token peek() {
        Token ret = nextToken();
        pushBack();
        return ret;
    }

    public Token getCurrentToken() {
        return mToken;
    }
}
