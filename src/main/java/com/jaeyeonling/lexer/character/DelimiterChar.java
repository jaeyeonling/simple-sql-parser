package com.jaeyeonling.lexer.character;

public record DelimiterChar(char value) {

    public DelimiterChar {
        if (isNotDelimiterChar(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 유효한 구분자가 아닙니다. '(', ')', ',', '.' 중 하나여야 합니다.".formatted(value)
            );
        }
    }

    public static boolean isDelimiterChar(final char c) {
        return c == '(' || c == ')' || c == ',' || c == '.';
    }

    public static boolean isNotDelimiterChar(final char c) {
        return !isDelimiterChar(c);
    }
}