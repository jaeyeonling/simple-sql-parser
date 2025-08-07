package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * 정수 리터럴을 나타냅니다.
 * 예: 42, 100, -5
 */
public record IntegerLiteral(
        int value,
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitIntegerLiteral(this);
    }
}
