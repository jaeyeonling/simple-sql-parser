package com.jaeyeonling.exception;

import com.jaeyeonling.lexer.Token;

/**
 * SQL 파싱 중 발생하는 모든 예외의 기본 클래스.
 */
public abstract class SqlParseException extends RuntimeException {

    protected SqlParseException(
            final String message,
            final Token token
    ) {
        super(formatMessageWithToken(message, token));
    }

    protected SqlParseException(
            final String message,
            final Token token,
            final Throwable cause
    ) {
        super(formatMessageWithToken(message, token), cause);
    }

    protected SqlParseException(final String message) {
        super(message);
    }

    protected SqlParseException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }

    private static String formatMessageWithToken(
            final String message,
            final Token token
    ) {
        return String.format("%s (위치: %d행 %d열, 토큰: '%s')",
                message, token.line(), token.column(), token.value());
    }
}
