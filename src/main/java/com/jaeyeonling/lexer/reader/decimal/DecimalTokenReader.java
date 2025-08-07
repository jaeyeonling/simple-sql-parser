package com.jaeyeonling.lexer.reader.decimal;

import com.jaeyeonling.lang.Digit;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;

/**
 * 소수점 숫자 토큰을 읽는 전략 구현체.
 * 소수점이 포함된 숫자만 처리합니다.
 */
public final class DecimalTokenReader implements TokenReader {

    private static final char DECIMAL_POINT = '.';

    @Override
    public boolean canRead(final CharStream charStream) {
        return startsWithDecimalPoint(charStream) || startsWithDigitAndHasDecimalPoint(charStream);
    }

    @Override
    public Token read(final CharStream charStream) {
        return DecimalToken.from(charStream).build();
    }

    private boolean startsWithDecimalPoint(final CharStream charStream) {
        return charStream.current() == DECIMAL_POINT && Digit.isDigit(charStream.peek());
    }

    private boolean startsWithDigitAndHasDecimalPoint(final CharStream charStream) {
        return Digit.isDigit(charStream.current()) && hasDecimalPointAhead(charStream);
    }

    private boolean hasDecimalPointAhead(final CharStream charStream) {
        return new DecimalPointScanner(charStream).scan();
    }
}
