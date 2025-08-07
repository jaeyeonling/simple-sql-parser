package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.visitor.AstVisitor;

/**
 * WHERE 절을 나타내는 AST 노드.
 */
public final class WhereClause extends AbstractClause {

    private final Expression condition;

    public WhereClause(
            final Expression condition,
            final SourceLocation location
    ) {
        super(location);
        this.condition = condition;
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.WHERE;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitWhereClause(this);
    }


    public Expression condition() {
        return condition;
    }
}
