package com.jaeyeonling.lexer.reader.decimal;

import com.jaeyeonling.lang.Digit;
import com.jaeyeonling.lexer.reader.CharStream;

/**
 * 소수 파싱 전략을 선택하고 실행하는 클래스
 */
record DecimalParser(CharStream charStream) {

    private static final char DECIMAL_POINT = '.';

    String parse() {
        if (startsWithDecimalPoint()) {
            return parseLeadingDecimalPoint();
        }
        return parseLeadingDigits();
    }

    private boolean startsWithDecimalPoint() {
        return charStream.current() == DECIMAL_POINT;
    }

    private String parseLeadingDecimalPoint() {
        return charStream.consume() + collectFractionalPart();
    }

    private String parseLeadingDigits() {
        return collectIntegerPart() +
                charStream.consume() +
                collectFractionalPart();
    }

    private String collectIntegerPart() {
        return charStream.collectWhile(Digit::isDigit);
    }

    private String collectFractionalPart() {
        return charStream.collectWhile(Digit::isDigit);
    }
}
