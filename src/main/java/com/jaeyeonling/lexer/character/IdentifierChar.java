package com.jaeyeonling.lexer.character;

import com.jaeyeonling.lang.AlphaNumeric;

public record IdentifierChar(char value) {

    public IdentifierChar {
        if (isNotIdentifierChar(value)) {
            throw new IllegalArgumentException(
                    "문자 '%c'는 식별자에 사용할 수 없습니다. 알파벳, 숫자 또는 언더스코어(_)만 허용됩니다.".formatted(value)
            );
        }
    }

    public static boolean isIdentifierChar(final char c) {
        return AlphaNumeric.isAlphaNumeric(c) || c == '_';
    }

    public static boolean isNotIdentifierChar(final char c) {
        return !isIdentifierChar(c);
    }
}
