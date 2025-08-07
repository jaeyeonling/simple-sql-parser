package com.jaeyeonling.lexer.reader.string;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

/**
 * 문자열 토큰을 빌드하는 내부 클래스
 */
record StringToken(
        Position startPosition,
        String content,
        int endIndex
) {

    static StringToken from(final CharStream charStream) {
        final Position position = charStream.position();
        final StringParser parser = new StringParser(charStream);
        final String content = parser.parse();
        final int endIndex = charStream.currentIndex();
        return new StringToken(
                position,
                content,
                endIndex
        );
    }

    Token build() {
        return new Token(
                TokenType.STRING,
                content,
                startPosition.line(),
                startPosition.column(),
                startPosition.index(),
                endIndex
        );
    }
}
