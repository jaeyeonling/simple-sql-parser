package com.jaeyeonling.lexer.reader.operator;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.character.DelimiterChar;
import com.jaeyeonling.lexer.character.OperatorChar;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;

/**
 * 연산자와 심볼 토큰을 읽는 전략 구현체.
 */
public final class OperatorTokenReader implements TokenReader {

    @Override
    public boolean canRead(final CharStream charStream) {
        return isOperatorOrSymbol(charStream.current());
    }

    @Override
    public Token read(final CharStream charStream) {
        return OperatorToken.from(charStream).build();
    }

    private boolean isOperatorOrSymbol(final char c) {
        return OperatorChar.isOperatorChar(c) ||
                DelimiterChar.isDelimiterChar(c) ||
                c == '*' ||
                c == '/';
    }
}
