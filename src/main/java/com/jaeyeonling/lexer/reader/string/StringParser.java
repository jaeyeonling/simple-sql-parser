package com.jaeyeonling.lexer.reader.string;

import com.jaeyeonling.exception.LexicalException;
import com.jaeyeonling.lexer.reader.CharStream;

/**
 * 문자열 파싱 로직을 담당하는 내부 클래스
 */
final class StringParser {

    private static final char QUOTE = '\'';
    private static final char BACKSLASH = '\\';

    private final CharStream charStream;

    StringParser(final CharStream charStream) {
        this.charStream = charStream;
    }

    String parse() {
        skipOpeningQuote();
        final String content = collectContent();
        skipClosingQuote();
        return content;
    }

    private void skipOpeningQuote() {
        charStream.expect(QUOTE);
    }

    private String collectContent() {
        final StringBuilder content = new StringBuilder();
        while (!isEndOfString()) {
            content.append(readNextCharacter());
        }

        return content.toString();
    }

    private boolean isEndOfString() {
        return charStream.isAtEnd() || isUnescapedQuote();
    }

    private boolean isUnescapedQuote() {
        return charStream.current() == QUOTE && !isSqlEscapedQuote();
    }

    private boolean isSqlEscapedQuote() {
        return charStream.peek() == QUOTE;
    }

    private char readNextCharacter() {
        if (isBackslashEscape()) {
            return readBackslashEscaped();
        }
        // SQL 이스케이프 따옴표는 현재 문자가 따옴표일 때만 체크
        if (charStream.current() == QUOTE && isSqlEscapedQuote()) {
            return readSqlEscapedQuote();
        }
        return charStream.consume();
    }

    private boolean isBackslashEscape() {
        return charStream.current() == BACKSLASH && charStream.peek() == QUOTE;
    }

    private char readBackslashEscaped() {
        charStream.advance(); // Skip backslash
        return charStream.consume(); // Return quote
    }

    private char readSqlEscapedQuote() {
        charStream.advance(); // Skip first quote
        return charStream.consume(); // Return second quote
    }

    private void skipClosingQuote() {
        if (charStream.isAtEnd()) {
            throw new LexicalException(
                    String.format("종료되지 않은 문자열 (위치: %d행 %d열)",
                            charStream.line(), charStream.column())
            );
        }
        charStream.expect(QUOTE);
    }
}
