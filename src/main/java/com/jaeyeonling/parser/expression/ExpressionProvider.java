package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.expression.Expression;

/**
 * 표현식 파싱을 제공하는 인터페이스.
 * 순환 의존성을 피하기 위해 사용됩니다.
 */
public interface ExpressionProvider {

    /**
     * 표현식을 파싱합니다.
     *
     * @return 파싱된 표현식
     */
    Expression parseExpression();

    /**
     * 덧셈/뺄셈 수준의 표현식을 파싱합니다.
     *
     * @return 파싱된 표현식
     */
    Expression parseAdditiveExpression();
}
