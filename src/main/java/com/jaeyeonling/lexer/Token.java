package com.jaeyeonling.lexer;

/**
 * SQL 토큰을 나타내는 클래스.
 *
 * @param type       토큰 타입
 * @param value      토큰 값
 * @param line       토큰이 시작하는 라인 번호 (1부터 시작)
 * @param column     토큰이 시작하는 컬럼 번호 (1부터 시작)
 * @param startIndex 소스 코드에서 토큰의 시작 위치 (0부터 시작)
 * @param endIndex   소스 코드에서 토큰의 끝 위치 (exclusive)
 */
public record Token(
        TokenType type,
        String value,
        int line,
        int column,
        int startIndex,
        int endIndex
) {
}
