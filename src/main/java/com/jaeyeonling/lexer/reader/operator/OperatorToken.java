package com.jaeyeonling.lexer.reader.operator;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

/**
 * 연산자 토큰을 빌드하는 내부 클래스
 */
record OperatorToken(
        Position position,
        Token token
) {

    static OperatorToken from(final CharStream charStream) {
        final Position startPosition = charStream.position();
        final OperatorParser parser = new OperatorParser(charStream, startPosition);
        final Token parsedToken = parser.parse();
        return new OperatorToken(startPosition, parsedToken);
    }

    Token build() {
        return token;
    }
}
