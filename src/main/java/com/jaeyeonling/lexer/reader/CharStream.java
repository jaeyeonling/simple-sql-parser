package com.jaeyeonling.lexer.reader;

import com.jaeyeonling.exception.LexicalException;

import java.util.function.Predicate;

/**
 * 문자 스트림을 관리하는 클래스.
 */
public final class CharStream {

    private final String input;
    private int current = 0;
    private int line = 1;
    private int column = 1;

    public CharStream(final String input) {
        this.input = input;
    }

    public boolean isAtEnd() {
        return current >= input.length();
    }

    public char current() {
        if (isAtEnd()) {
            return '\0';
        }

        return input.charAt(current);
    }

    public char peek() {
        return peek(1);
    }

    public char peek(final int n) {
        if (current + n >= input.length()) {
            return '\0';
        }

        return input.charAt(current + n);
    }

    public void advance() {
        if (isAtEnd()) {
            return;
        }

        if (current() == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        current++;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public int currentIndex() {
        return current;
    }

    public boolean match(final char expected) {
        return !isAtEnd() && current() == expected;
    }

    public boolean advanceIfMatch(final char expected) {
        if (match(expected)) {
            advance();
            return true;
        }

        return false;
    }

    /**
     * 조건을 만족하는 동안 문자를 소비합니다.
     */
    public void consumeWhile(final Predicate<Character> condition) {
        while (!isAtEnd() && condition.test(current())) {
            advance();
        }
    }

    /**
     * 조건을 만족하는 동안 문자를 수집합니다.
     */
    public String collectWhile(final Predicate<Character> condition) {
        final StringBuilder result = new StringBuilder();
        while (!isAtEnd() && condition.test(current())) {
            result.append(current());
            advance();
        }
        return result.toString();
    }

    /**
     * 특정 문자를 기대하고 소비합니다.
     */
    public void expect(final char expected) {
        if (current() != expected) {
            throw new LexicalException(
                    String.format("""
                                    예상한 문자와 다른 문자를 발견했습니다.
                                    예상: '%c'
                                    실제: '%c'
                                    위치: %d행 %d열""",
                            expected, current(), line, column)
            );
        }
        advance();
    }

    /**
     * 현재 문자를 소비하고 반환합니다.
     */
    public char consume() {
        final char c = current();
        advance();
        return c;
    }

    /**
     * 현재 위치 정보를 반환합니다.
     */
    public Position position() {
        return new Position(line, column, current);
    }
}
