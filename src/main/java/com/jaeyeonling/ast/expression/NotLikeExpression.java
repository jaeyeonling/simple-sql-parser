package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * NOT LIKE 표현식을 나타냅니다.
 * 예: name NOT LIKE '%test%'
 */
public record NotLikeExpression(
        Expression expression,
        Expression pattern,
        SourceLocation location
) implements PatternMatcher {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitNotLikeExpression(this);
    }
}
