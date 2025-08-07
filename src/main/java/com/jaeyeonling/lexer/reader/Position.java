package com.jaeyeonling.lexer.reader;

/**
 * 위치 정보를 담는 불변 레코드
 */
public record Position(
        int line,
        int column,
        int index
) {
}
