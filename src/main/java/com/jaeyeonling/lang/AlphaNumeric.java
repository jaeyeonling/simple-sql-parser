package com.jaeyeonling.lang;

public record AlphaNumeric(char value) {

    public AlphaNumeric {
        if (isNotAlphaNumeric(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 유효한 알파벳 또는 숫자가 아닙니다. 'a-z', 'A-Z' 또는 '0-9'만 허용됩니다.".formatted(value)
            );
        }
    }

    public static boolean isAlphaNumeric(final char c) {
        return Alpha.isAlpha(c) || Digit.isDigit(c);
    }

    public static boolean isNotAlphaNumeric(final char c) {
        return !isAlphaNumeric(c);
    }

    public boolean isAlpha() {
        return Alpha.isAlpha(value);
    }

    public boolean isDigit() {
        return Digit.isDigit(value);
    }
}