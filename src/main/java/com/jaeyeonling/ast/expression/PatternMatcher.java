package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;

/**
 * 패턴 매칭 표현식을 위한 공통 인터페이스.
 * LIKE와 NOT LIKE 표현식이 이를 구현합니다.
 */
public interface PatternMatcher extends Expression {

    /**
     * 패턴 매칭할 대상 표현식
     */
    Expression expression();

    /**
     * 매칭할 패턴
     */
    Expression pattern();

    /**
     * 소스 위치
     */
    SourceLocation location();

    @Override
    default ExpressionType expressionType() {
        return ExpressionType.COMPARISON;
    }
}
