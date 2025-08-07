package com.jaeyeonling.parser;

import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.Token;

/**
 * 파서 에러 처리를 전담하는 클래스.
 */
public final class ParserErrorHandler {

    /**
     * EOF 검증을 수행합니다.
     * 모든 토큰이 소비되지 않았다면 적절한 예외를 발생시킵니다.
     *
     * @param tokenStream 토큰 스트림
     * @throws SyntaxException 파싱되지 않은 토큰이 남아있는 경우
     */
    public void enforceEndOfFile(final TokenStream tokenStream) {
        if (!tokenStream.isAtEnd()) {
            final Token unexpectedToken = tokenStream.peek();
            throw createUnexpectedTokenException(unexpectedToken);
        }
    }

    /**
     * 예상치 못한 토큰에 대한 구체적인 예외를 생성합니다.
     * <p>
     * PostgreSQL, Calcite 등의 파서처럼 토큰 타입에 따라
     * 맞춤형 에러 메시지를 제공합니다.
     *
     * @param token 예상치 못한 토큰
     * @return 구체적인 에러 메시지를 포함한 예외
     */
    private SyntaxException createUnexpectedTokenException(final Token token) {
        return switch (token.type()) {
            case IS -> new SyntaxException(
                    "IS 키워드 뒤에는 NULL 또는 NOT NULL이 와야 합니다.\n" +
                            "예시: email IS NULL, name IS NOT NULL",
                    token
            );
            case WHERE, GROUP, HAVING, ORDER, LIMIT -> createClauseOrderException(token);
            case DOT -> createUnsupportedFeatureException(
                    "점(.) 문법은 지원하지 않습니다.\n" +
                            "스키마.테이블 형식은 아직 구현되지 않았습니다.",
                    token
            );
            case LPAREN -> createUnsupportedFeatureException(
                    "함수 호출은 지원하지 않습니다.\n" +
                            "COUNT(*), SUM(), AVG() 등의 집계 함수는 아직 구현되지 않았습니다.",
                    token
            );
            case RPAREN -> new SyntaxException(
                    "닫는 괄호 ')'가 예상치 못한 위치에 있습니다.\n" +
                            "괄호의 짝이 맞는지 확인해주세요.",
                    token
            );
            case COMMA -> new SyntaxException(
                    "쉼표(,)가 예상치 못한 위치에 있습니다.\n" +
                            "SELECT 절이나 FROM 절의 항목을 구분할 때 사용해주세요.",
                    token
            );
            default -> createGenericUnexpectedTokenException(token);
        };
    }

    /**
     * SQL 절 순서 오류에 대한 예외를 생성합니다.
     */
    private SyntaxException createClauseOrderException(final Token token) {
        return new SyntaxException(String.format("""
                        '%s' 절이 잘못된 위치에 있습니다.
                        올바른 SQL 절 순서:
                          SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT
                        현재 위치: %d행 %d열""",
                token.value(),
                token.line(),
                token.column()),
                token
        );
    }

    /**
     * 미구현 기능에 대한 예외를 생성합니다.
     */
    private SyntaxException createUnsupportedFeatureException(
            final String message,
            final Token token
    ) {
        return new SyntaxException(
                message + String.format(
                        "\n위치: %d행 %d열",
                        token.line(),
                        token.column()
                ),
                token
        );
    }

    /**
     * 일반적인 예상치 못한 토큰 예외를 생성합니다.
     */
    private SyntaxException createGenericUnexpectedTokenException(final Token token) {
        return new SyntaxException(String.format("""
                        예상치 못한 토큰 '%s'이(가) 있습니다.
                        위치: %d행 %d열
                        SQL 문법을 확인해주세요.""",
                token.value(),
                token.line(),
                token.column()),
                token
        );
    }
}
