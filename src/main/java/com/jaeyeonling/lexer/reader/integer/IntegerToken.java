package com.jaeyeonling.lexer.reader.integer;

import com.jaeyeonling.lang.Digit;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

/**
 * 정수 토큰을 빌드하는 내부 빌더 클래스
 */
record IntegerToken(
        Position position,
        String digits,
        int endIndex
) {

    static IntegerToken from(final CharStream charStream) {
        final Position startPosition = charStream.position();
        final String collectedDigits = charStream.collectWhile(Digit::isDigit);
        final int endIndex = charStream.currentIndex();
        return new IntegerToken(
                startPosition,
                collectedDigits,
                endIndex
        );
    }

    Token build() {
        return new Token(
                TokenType.INTEGER,
                digits,
                position.line(),
                position.column(),
                position.index(),
                endIndex
        );
    }
}
