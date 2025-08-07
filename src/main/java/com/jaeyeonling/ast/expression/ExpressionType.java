package com.jaeyeonling.ast.expression;

/**
 * 표현식 타입을 나타내는 열거형
 */
public enum ExpressionType {
    COLUMN,             // 컬럼 참조
    LITERAL,            // 리터럴 값
    BINARY_OPERATOR,    // 이항 연산자
    UNARY_OPERATOR,     // 단항 연산자
    COMPARISON,         // 비교 연산자 (LIKE, IN, BETWEEN, IS NULL)
    FUNCTION_CALL,      // 함수 호출
    SUBQUERY,           // 서브쿼리
    CASE_WHEN          // CASE WHEN 표현식
}
