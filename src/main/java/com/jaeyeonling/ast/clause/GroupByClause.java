package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * GROUP BY 절을 나타내는 AST 노드.
 */
public final class GroupByClause extends AbstractClause {

    private final List<Expression> groupingExpressions;

    public GroupByClause(
            final List<Expression> groupingExpressions,
            final SourceLocation location
    ) {
        super(location);
        this.groupingExpressions = unmodifiableList(groupingExpressions);
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.GROUP_BY;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitGroupByClause(this);
    }

    public List<Expression> groupingExpressions() {
        return groupingExpressions;
    }
}
