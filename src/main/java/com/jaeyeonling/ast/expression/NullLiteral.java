package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * NULL 리터럴을 나타냅니다.
 */
public record NullLiteral(
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitNullLiteral(this);
    }
}
