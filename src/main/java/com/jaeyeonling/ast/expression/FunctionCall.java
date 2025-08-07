package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.List;

/**
 * 함수 호출 표현식.
 * COUNT(*), SUM(column), AVG(expression) 등을 표현합니다.
 */
public record FunctionCall(
        String functionName,
        List<Expression> arguments,  // COUNT(*)는 AllColumns, SUM(col)은 ColumnReference
        SourceLocation location
) implements Expression {

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.FUNCTION_CALL;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }

    /**
     * 집계 함수인지 확인합니다.
     */
    public boolean isAggregateFunction() {
        return List.of("COUNT", "SUM", "AVG", "MIN", "MAX")
                .contains(functionName.toUpperCase());
    }
}