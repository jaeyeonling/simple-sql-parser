package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * 문자열 리터럴을 나타냅니다.
 * 예: 'hello', 'world'
 */
public record StringLiteral(
        String value,
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitStringLiteral(this);
    }
}
