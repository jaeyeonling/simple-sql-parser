package com.jaeyeonling.lexer.character;

import com.jaeyeonling.lang.Alpha;

public record IdentifierStart(char value) {

    public IdentifierStart {
        if (isNotIdentifierStart(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 식별자의 시작 문자로 사용할 수 없습니다. 알파벳 또는 언더스코어(_)만 허용됩니다.".formatted(value)
            );
        }
    }

    public static boolean isIdentifierStart(final char c) {
        return Alpha.isAlpha(c) || c == '_';
    }

    public static boolean isNotIdentifierStart(final char c) {
        return !isIdentifierStart(c);
    }
}