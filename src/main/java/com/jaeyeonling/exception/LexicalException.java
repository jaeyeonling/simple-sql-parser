package com.jaeyeonling.exception;

import com.jaeyeonling.lexer.Token;

/**
 * 렉싱(토큰화) 중 발생하는 예외.
 * 예: 잘못된 문자, 종료되지 않은 문자열 등
 */
public final class LexicalException extends SqlParseException {

    public LexicalException(
            final String message,
            final Token token
    ) {
        super(message, token);
    }

    public LexicalException(final String message) {
        super(message);
    }
}
