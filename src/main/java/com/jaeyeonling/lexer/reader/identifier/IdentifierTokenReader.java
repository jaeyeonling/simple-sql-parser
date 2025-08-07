package com.jaeyeonling.lexer.reader.identifier;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.character.IdentifierStart;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;

/**
 * 식별자와 키워드 토큰을 읽는 전략 구현체.
 */
public final class IdentifierTokenReader implements TokenReader {

    @Override
    public boolean canRead(final CharStream charStream) {
        return isIdentifierStart(charStream);
    }

    @Override
    public Token read(final CharStream charStream) {
        return IdentifierOrKeyword.from(charStream).toToken();
    }

    private boolean isIdentifierStart(final CharStream charStream) {
        return IdentifierStart.isIdentifierStart(charStream.current());
    }
}
