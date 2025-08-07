package com.jaeyeonling.lexer.reader.decimal;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

/**
 * 소수점 토큰을 빌드하는 내부 클래스
 * - 두 가지 형태의 소수를 처리: .789 형태와 123.456 형태
 */
record DecimalToken(
        Position position,
        String value,
        int endIndex
) {
    static DecimalToken from(final CharStream charStream) {
        final Position startPosition = charStream.position();
        final DecimalParser parser = new DecimalParser(charStream);
        final String decimalValue = parser.parse();
        final int endIndex = charStream.currentIndex();
        return new DecimalToken(startPosition, decimalValue, endIndex);
    }

    Token build() {
        return new Token(
                TokenType.DECIMAL,
                value,
                position.line(),
                position.column(),
                position.index(),
                endIndex
        );
    }
}
