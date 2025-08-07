package com.jaeyeonling.lexer.reader.string;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;

/**
 * 문자열 리터럴 토큰을 읽는 전략 구현체.
 */
public final class StringTokenReader implements TokenReader {

    private static final char QUOTE = '\'';

    @Override
    public boolean canRead(final CharStream charStream) {
        return startsWithQuote(charStream);
    }

    @Override
    public Token read(final CharStream charStream) {
        return StringToken.from(charStream).build();
    }

    private boolean startsWithQuote(final CharStream charStream) {
        return charStream.current() == QUOTE;
    }
}
