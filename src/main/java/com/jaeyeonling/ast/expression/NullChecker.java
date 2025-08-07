package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;

/**
 * NULL 체크 표현식을 위한 공통 인터페이스.
 * IS NULL과 IS NOT NULL 표현식이 이를 구현합니다.
 */
public interface NullChecker extends Expression {

    /**
     * NULL 체크할 대상 표현식
     */
    Expression expression();

    /**
     * 소스 위치
     */
    SourceLocation location();

    @Override
    default ExpressionType expressionType() {
        return ExpressionType.COMPARISON;
    }
}
