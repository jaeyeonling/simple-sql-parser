package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * 이항 연산자 표현식을 나타냅니다.
 * 예: a = b, x > 10, name LIKE '%test%'
 */
public record BinaryOperatorExpression(
        Expression left,
        Operator operator,
        Expression right,
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.BINARY_OPERATOR;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitBinaryOperatorExpression(this);
    }
}
