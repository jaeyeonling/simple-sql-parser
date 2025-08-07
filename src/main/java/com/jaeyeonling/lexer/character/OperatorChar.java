package com.jaeyeonling.lexer.character;

public record OperatorChar(char value) {

    public OperatorChar {
        if (isNotOperatorChar(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 유효한 연산자 문자가 아닙니다. =, !, <, >, +, -, *, / 중 하나여야 합니다.".formatted(value)
            );
        }
    }

    public static boolean isOperatorChar(final char c) {
        return c == '=' || c == '!' || c == '<' || c == '>' ||
                c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static boolean isNotOperatorChar(final char c) {
        return !isOperatorChar(c);
    }
}
