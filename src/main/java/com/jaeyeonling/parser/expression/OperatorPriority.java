package com.jaeyeonling.parser.expression;

/**
 * 연산자 파싱 우선순위를 정의합니다.
 * 값이 작을수록 먼저 처리됩니다.
 */
public enum OperatorPriority {
    IS_NULL(10),         // IS NULL, IS NOT NULL - 가장 높은 우선순위
    SPECIAL(20),         // LIKE, IN, BETWEEN - 특수 연산자
    COMPARISON(30),      // =, !=, <, >, <=, >= - 일반 비교
    ARITHMETIC_MUL(40),  // * / - 곱셈/나눗셈
    ARITHMETIC_ADD(50),  // + - - 덧셈/뺄셈
    LOGICAL_AND(60),     // AND
    LOGICAL_OR(70);      // OR - 가장 낮은 우선순위

    private final int value;

    OperatorPriority(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
