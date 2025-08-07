package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * BETWEEN 표현식을 나타냅니다.
 * 예: age BETWEEN 18 AND 65
 */
public record BetweenExpression(
        Expression expression,
        Expression lowerBound,
        Expression upperBound,
        SourceLocation location
) implements RangeMatcher {

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBetweenExpression(this);
    }
}
