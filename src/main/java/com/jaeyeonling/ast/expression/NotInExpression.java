package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.List;

/**
 * NOT IN 표현식을 나타냅니다.
 * 예: id NOT IN (1, 2, 3)
 */
public record NotInExpression(
        Expression expression,
        List<Expression> values,
        SourceLocation location
) implements ValueListMatcher {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitNotInExpression(this);
    }
}
