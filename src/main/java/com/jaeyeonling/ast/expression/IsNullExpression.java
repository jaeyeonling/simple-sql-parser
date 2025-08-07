package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * IS NULL 표현식을 나타냅니다.
 * 예: email IS NULL
 */
public record IsNullExpression(
        Expression expression,
        SourceLocation location
) implements NullChecker {

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitIsNullExpression(this);
    }
}
