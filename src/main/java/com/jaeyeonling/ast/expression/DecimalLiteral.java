package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * 소수점 리터럴을 나타내는 클래스.
 */
public record DecimalLiteral(
        double value,
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.LITERAL;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitDecimalLiteral(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
