package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.Optional;

/**
 * 모든 컬럼을 선택하는 * 표현식을 나타냅니다.
 * SELECT 절에서는 SelectItem으로, COUNT(*)에서는 Expression으로 사용됩니다.
 */
public record AllColumns(SourceLocation location) implements SelectItem, Expression {

    @Override
    public Optional<String> alias() {
        return Optional.empty();  // *는 별칭을 가질 수 없음
    }

    @Override
    public SelectItemType selectItemType() {
        return SelectItemType.ALL_COLUMNS;
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.COLUMN;  // *는 특별한 형태의 컬럼 참조
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitAllColumns(this);
    }
}
