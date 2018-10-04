package com.putable.videx.core.oio.load;

import com.putable.videx.core.oio.load.AST.ASTTree;

public class Parser {
    private final Lexer mLexer;
    private final OIOCompiler mCompiler;

    public OIOCompiler getCompiler() { return mCompiler; }
    
    public Token next() {
        return mLexer.nextToken();
    }

    public void pushBack() {
        mLexer.pushBack();
    }

    public Parser(OIOCompiler compiler, Lexer lex) {
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
