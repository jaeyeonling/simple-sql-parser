package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;

/**
 * 범위 매칭 표현식을 위한 공통 인터페이스.
 * BETWEEN과 NOT BETWEEN 표현식이 이를 구현합니다.
 */
public interface RangeMatcher extends Expression {

    /**
     * 범위를 체크할 대상 표현식
     */
    Expression expression();

    /**
     * 범위의 하한값
     */
    Expression lowerBound();

    /**
     * 범위의 상한값
     */
    Expression upperBound();

    /**
     * 소스 위치
     */
    SourceLocation location();

    @Override
    default ExpressionType expressionType() {
        return ExpressionType.COMPARISON;
    }
}
