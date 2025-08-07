package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.AstNode;

/**
 * SQL 표현식을 나타내는 인터페이스.
 * 컬럼, 리터럴, 함수 호출, 연산자 등 모든 표현식이 이 인터페이스를 구현합니다.
 */
public interface Expression extends AstNode {

    /**
     * 표현식의 타입을 반환합니다.
     *
     * @return 표현식 타입
     */
    ExpressionType expressionType();
}
