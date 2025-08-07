package com.jaeyeonling.exception;

import com.jaeyeonling.lexer.Token;

/**
 * 구문 분석 중 발생하는 예외.
 * 예: 잘못된 SQL 구조, 누락된 키워드 등
 */
public final class SyntaxException extends SqlParseException {

    public SyntaxException(
            final String message,
            final Token token
    ) {
        super(message, token);
    }

    public SyntaxException(final String message) {
        super(message);
    }
}
