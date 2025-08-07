package com.jaeyeonling.lexer.reader.decimal;

import com.jaeyeonling.lang.Digit;
import com.jaeyeonling.lexer.reader.CharStream;

/**
 * 소수점 존재 여부를 스캔하는 클래스
 * - CharStream의 위치를 변경하지 않음
 * - 미리보기(lookahead) 기능 제공
 */
record DecimalPointScanner(CharStream charStream) {

    private static final char DECIMAL_POINT = '.';

    boolean scan() {
        final int offset = skipLeadingDigits();
        return checkForDecimalPointFollowedByDigit(offset);
    }

    private boolean checkForDecimalPointFollowedByDigit(final int offset) {
        return peekAt(offset) == DECIMAL_POINT && Digit.isDigit(peekAt(offset + 1));
    }

    private int skipLeadingDigits() {
        int offset = 0;
        while (Digit.isDigit(peekAt(offset))) {
            offset++;
        }
        return offset;
    }

    private char peekAt(final int offset) {
        return charStream.peek(offset);
    }
}
