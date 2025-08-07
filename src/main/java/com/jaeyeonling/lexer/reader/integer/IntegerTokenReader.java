package com.jaeyeonling.lexer.reader.integer;

import com.jaeyeonling.lang.Digit;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;

/**
 * 정수 토큰을 읽는 전략 구현체.
 * 10진수 정수만 처리합니다.
 */
public final class IntegerTokenReader implements TokenReader {

    @Override
    public boolean canRead(final CharStream charStream) {
        return startsWithDigit(charStream);
    }

    @Override
    public Token read(final CharStream charStream) {
        return IntegerToken.from(charStream).build();
    }

    private boolean startsWithDigit(final CharStream charStream) {
        return Digit.isDigit(charStream.current());
    }
}
