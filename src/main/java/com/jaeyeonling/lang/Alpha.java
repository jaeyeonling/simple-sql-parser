package com.jaeyeonling.lang;

public record Alpha(char value) {

    public Alpha {
        if (isNotAlpha(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 유효한 알파벳이 아닙니다. 'a-z' 또는 'A-Z'만 허용됩니다.".formatted(value)
            );
        }
    }

    public static boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isNotAlpha(final char c) {
        return !isAlpha(c);
    }

    public boolean isUpperCase() {
        return value >= 'A' && value <= 'Z';
    }

    public boolean isLowerCase() {
        return value >= 'a' && value <= 'z';
    }
}