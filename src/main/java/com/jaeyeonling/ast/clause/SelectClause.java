package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.SelectItem;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.Collections;
import java.util.List;

/**
 * SELECT 절을 나타내는 AST 노드.
 * DISTINCT 옵션과 선택할 항목들을 포함합니다.
 */
public final class SelectClause extends AbstractClause {

    private final boolean distinct;
    private final List<SelectItem> selectItems;

    public SelectClause(
            final boolean distinct,
            final List<SelectItem> selectItems,
            final SourceLocation location
    ) {
        super(location);
        this.distinct = distinct;
        this.selectItems = Collections.unmodifiableList(selectItems);
    }

    public static SelectClause of(
            final List<SelectItem> selectItems,
            final SourceLocation location
    ) {
        return new SelectClause(false, selectItems, location);
    }

    public static SelectClause distinct(
            final List<SelectItem> selectItems,
            final SourceLocation location
    ) {
        return new SelectClause(true, selectItems, location);
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.SELECT;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitSelectClause(this);
    }


    public boolean isDistinct() {
        return distinct;
    }

    public List<SelectItem> selectItems() {
        return selectItems;
    }
}
