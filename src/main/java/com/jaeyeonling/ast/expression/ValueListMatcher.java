package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;

import java.util.List;

/**
 * 값 목록 매칭 표현식을 위한 공통 인터페이스.
 * IN과 NOT IN 표현식이 이를 구현합니다.
 */
public interface ValueListMatcher extends Expression {

    /**
     * 값 목록과 비교할 대상 표현식
     */
    Expression expression();

    /**
     * 비교할 값 목록
     */
    List<Expression> values();

    /**
     * 소스 위치
     */
    SourceLocation location();

    @Override
    default ExpressionType expressionType() {
        return ExpressionType.COMPARISON;
    }
}
