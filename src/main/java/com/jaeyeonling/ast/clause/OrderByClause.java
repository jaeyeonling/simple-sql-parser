package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * ORDER BY 절을 나타내는 AST 노드.
 */
public final class OrderByClause extends AbstractClause {

    private final List<OrderByItem> orderByItems;

    public OrderByClause(
            final List<OrderByItem> orderByItems,
            final SourceLocation location
    ) {
        super(location);
        this.orderByItems = unmodifiableList(orderByItems);
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.ORDER_BY;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitOrderByClause(this);
    }


    public List<OrderByItem> orderByItems() {
        return orderByItems;
    }
}
