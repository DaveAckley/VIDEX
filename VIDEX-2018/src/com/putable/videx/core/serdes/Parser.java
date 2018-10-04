package com.putable.videx.core.serdes;

import com.putable.videx.core.serdes.AST.ASTTree;

public class Parser {
    private final Lexer mLexer;
    private final Compiler mCompiler;

    public Compiler getCompiler() { return mCompiler; }
    
    public Token next() {
        return mLexer.nextToken();
    }

    public void pushBack() {
        mLexer.pushBack();
    }

    public Parser(Compiler compiler, Lexer lex) {
        this.mLexer = lex;
        this.mCompiler = compiler;
    }

    public ASTTree parse() {
        ASTTree tree = ASTTree.parse(this);
        return tree;
    }

    public Token require(TokType t) {
        Token next = next();
        if (next.toktype != t)
            throw new ParseException(next,
                    "Required token type " + t + " but found " + next);
        return next;
    }
}
