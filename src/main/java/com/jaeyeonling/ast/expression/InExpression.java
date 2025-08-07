package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.List;

/**
 * IN 표현식을 나타냅니다.
 * 예: status IN ('active', 'pending')
 */
public record InExpression(
        Expression expression,
        List<Expression> values,
        SourceLocation location
) implements ValueListMatcher {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitInExpression(this);
    }
}
