package com.jaeyeonling.lexer;

import com.jaeyeonling.lang.Whitespace;
import com.jaeyeonling.lexer.reader.CharStream;

/**
 * 공백과 주석 처리를 담당하는 클래스
 * - SQL 주석 (--) 처리
 * - 모든 종류의 공백 문자 처리
 */
final class WhitespaceSkipper {
    private final CharStream charStream;

    WhitespaceSkipper(final CharStream charStream) {
        this.charStream = charStream;
    }

    /**
     * 모든 공백과 주석을 건너뜁니다.
     */
    void skipAll() {
        while (canSkip()) {
            skipNext();
        }
    }

    private boolean canSkip() {
        return !charStream.isAtEnd() &&
                (isWhitespace() || isCommentStart());
    }

    private void skipNext() {
        if (isWhitespace()) {
            skipWhitespace();
        } else if (isCommentStart()) {
            skipComment();
        }
    }

    private boolean isWhitespace() {
        return Whitespace.isWhitespace(charStream.current());
    }

    private void skipWhitespace() {
        charStream.consumeWhile(Whitespace::isWhitespace);
    }

    private boolean isCommentStart() {
        return charStream.current() == '-' &&
                charStream.peek() == '-';
    }

    private void skipComment() {
        skipCommentPrefix();
        skipUntilEndOfLine();
    }

    private void skipCommentPrefix() {
        charStream.advance(); // First '-'
        charStream.advance(); // Second '-'
    }

    private void skipUntilEndOfLine() {
        charStream.consumeWhile(c -> c != '\n');
    }
}
