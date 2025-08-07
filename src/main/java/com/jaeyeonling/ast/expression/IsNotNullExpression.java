package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * IS NOT NULL 표현식을 나타냅니다.
 * 예: name IS NOT NULL
 */
public record IsNotNullExpression(
        Expression expression,
        SourceLocation location
) implements NullChecker {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitIsNotNullExpression(this);
    }
}
