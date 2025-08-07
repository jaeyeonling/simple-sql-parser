package com.jaeyeonling.lang;

public record Whitespace(char value) {

    public Whitespace {
        if (isNotWhitespace(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c' (ASCII: %d)는 유효한 공백 문자가 아닙니다. 스페이스, 탭, 개행문자만 허용됩니다.".formatted(value, (int) value)
            );
        }
    }

    public static boolean isWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static boolean isNotWhitespace(final char c) {
        return !isWhitespace(c);
    }
}
