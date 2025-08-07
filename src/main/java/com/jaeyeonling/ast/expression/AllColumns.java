package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.Optional;

/**
 * 모든 컬럼을 선택하는 * 표현식을 나타냅니다.
 */
public record AllColumns(SourceLocation location) implements SelectItem {

    @Override
    public Optional<String> alias() {
        return Optional.empty();  // *는 별칭을 가질 수 없음
    }

    @Override
    public SelectItemType selectItemType() {
        return SelectItemType.ALL_COLUMNS;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitAllColumns(this);
    }
}
