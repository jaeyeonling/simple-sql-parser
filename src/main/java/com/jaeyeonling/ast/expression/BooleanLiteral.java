package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * Boolean 리터럴을 나타냅니다 (TRUE, FALSE).
 */
public record BooleanLiteral(
        boolean value,
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBooleanLiteral(this);
    }
}