package com.jaeyeonling.lexer;

import java.util.EnumSet;
import java.util.Set;

/**
 * 토큰 타입
 */
public enum TokenType {

    // Keywords
    SELECT("SELECT"),
    FROM("FROM"),
    WHERE("WHERE"),
    AS("AS"),
    DISTINCT("DISTINCT"),
    GROUP("GROUP"),
    BY("BY"),
    HAVING("HAVING"),
    ORDER("ORDER"),
    LIMIT("LIMIT"),
    OFFSET("OFFSET"),
    ASC("ASC"),
    DESC("DESC"),
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    BETWEEN("BETWEEN"),
    LIKE("LIKE"),
    IS("IS"),
    NULL("NULL"),
    TRUE("TRUE"),
    FALSE("FALSE"),

    // Identifiers and Literals
    IDENTIFIER("IDENTIFIER"),
    INTEGER("INTEGER"),
    DECIMAL("DECIMAL"),
    STRING("STRING"),

    // Operators
    EQUALS("="),
    NOT_EQUALS("!="),
    LESS_THAN("<"),
    LESS_THAN_OR_EQUALS("<="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUALS(">="),
    PLUS("+"),
    MINUS("-"),

    // Symbols
    STAR("*"),
    SLASH("/"),
    COMMA(","),
    DOT("."),
    LPAREN("("),
    RPAREN(")"),

    // Special
    EOF("EOF");

    // SQL 키워드 집합 (IDENTIFIER, INTEGER, DECIMAL, STRING 제외)
    private static final Set<TokenType> KEYWORDS = EnumSet.of(
            SELECT, FROM, WHERE, AS, DISTINCT,
            GROUP, BY, HAVING, ORDER,
            LIMIT, OFFSET, ASC, DESC,
            AND, OR, NOT, IN, BETWEEN, LIKE, IS,
            NULL, TRUE, FALSE
    );
    // 절을 시작하는 키워드
    private static final Set<TokenType> CLAUSE_STARTERS = EnumSet.of(
            SELECT, FROM, WHERE, GROUP, ORDER, HAVING, LIMIT
    );
    // 논리 연산자
    private static final Set<TokenType> LOGICAL_OPERATORS = EnumSet.of(
            AND, OR, NOT
    );
    // 비교 연산자
    private static final Set<TokenType> COMPARISON_OPERATORS = EnumSet.of(
            EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS,
            GREATER_THAN, GREATER_THAN_OR_EQUALS,
            IN, BETWEEN, LIKE, IS
    );
    // 리터럴 값
    private static final Set<TokenType> LITERAL_VALUES = EnumSet.of(
            NULL, TRUE, FALSE, INTEGER, DECIMAL, STRING
    );
    // 단일 문자 심볼 (연산자 및 구분자)
    private static final Set<TokenType> SINGLE_CHAR_SYMBOLS = EnumSet.of(
            EQUALS, LESS_THAN, GREATER_THAN,
            PLUS, MINUS,
            STAR, SLASH, COMMA, DOT,
            LPAREN, RPAREN
    );
    private final String symbol;

    TokenType(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    /**
     * SQL 키워드인지 확인합니다.
     * IDENTIFIER, INTEGER, DECIMAL, STRING은 키워드가 아닙니다.
     */
    public boolean isKeyword() {
        return KEYWORDS.contains(this);
    }

    /**
     * 절을 시작하는 키워드인지 확인합니다.
     */
    public boolean isClauseStarter() {
        return CLAUSE_STARTERS.contains(this);
    }

    /**
     * 논리 연산자인지 확인합니다.
     */
    public boolean isLogicalOperator() {
        return LOGICAL_OPERATORS.contains(this);
    }

    /**
     * 비교 연산자인지 확인합니다.
     */
    public boolean isComparisonOperator() {
        return COMPARISON_OPERATORS.contains(this);
    }

    /**
     * 리터럴 값인지 확인합니다.
     */
    public boolean isLiteral() {
        return LITERAL_VALUES.contains(this);
    }

    /**
     * 단일 문자 심볼인지 확인합니다.
     */
    public boolean isSingleCharSymbol() {
        return SINGLE_CHAR_SYMBOLS.contains(this);
    }

    /**
     * 단일 문자 심볼인 경우 해당 문자를 반환합니다.
     */
    public char asChar() {
        if (!isSingleCharSymbol()) {
            throw new IllegalStateException(
                    String.format("TokenType %s는 단일 문자 심볼이 아닙니다", this));
        }
        return symbol.charAt(0);
    }
}
