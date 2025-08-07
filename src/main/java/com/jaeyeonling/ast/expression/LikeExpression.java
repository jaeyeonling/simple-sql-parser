package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * LIKE 표현식을 나타냅니다.
 * 예: name LIKE 'John%'
 */
public record LikeExpression(
        Expression expression,
        Expression pattern,
        SourceLocation location
) implements PatternMatcher {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitLikeExpression(this);
    }
}
