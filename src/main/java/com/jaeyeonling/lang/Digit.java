package com.jaeyeonling.lang;

public record Digit(char value) {

    public Digit {
        if (isNotDigit(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 유효한 숫자가 아닙니다. '0'부터 '9'까지의 문자만 허용됩니다.".formatted(value)
            );
        }
    }

    public static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isNotDigit(final char c) {
        return !isDigit(c);
    }
}
