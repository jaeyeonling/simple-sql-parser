package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * NOT BETWEEN 표현식을 나타냅니다.
 * 예: price NOT BETWEEN 0 AND 100
 */
public record NotBetweenExpression(
        Expression expression,
        Expression lowerBound,
        Expression upperBound,
        SourceLocation location
) implements RangeMatcher {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitNotBetweenExpression(this);
    }
}
