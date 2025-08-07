package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.table.TableReference;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * FROM 절을 나타내는 AST 노드.
 * 하나 이상의 테이블 참조를 포함합니다.
 */
public final class FromClause extends AbstractClause {

    private final TableReferences tableReferences;

    private FromClause(
            final TableReferences tableReferences,
            final SourceLocation location
    ) {
        super(location);
        this.tableReferences = tableReferences;
    }

    public FromClause(
            final List<TableReference> tableReferences,
            final SourceLocation location
    ) {
        this(new TableReferences(unmodifiableList(tableReferences)), location);
    }

    public FromClause(
            final TableReference tableReference,
            final SourceLocation location
    ) {
        this(Collections.singletonList(tableReference), location);
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.FROM;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitFromClause(this);
    }


    public List<TableReference> tableReferences() {
        return tableReferences.values();
    }

    private record TableReferences(List<TableReference> values) {
        TableReferences {
            if (values == null || values.isEmpty()) {
                throw new IllegalArgumentException("FROM 절은 하나 이상의 테이블 참조를 포함해야 합니다.");
            }
        }
    }
}
