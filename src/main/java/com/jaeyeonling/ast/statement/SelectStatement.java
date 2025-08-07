package com.jaeyeonling.ast.statement;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.ast.clause.Clause;
import com.jaeyeonling.ast.clause.FromClause;
import com.jaeyeonling.ast.clause.GroupByClause;
import com.jaeyeonling.ast.clause.HavingClause;
import com.jaeyeonling.ast.clause.LimitClause;
import com.jaeyeonling.ast.clause.OrderByClause;
import com.jaeyeonling.ast.clause.SelectClause;
import com.jaeyeonling.ast.clause.WhereClause;
import com.jaeyeonling.visitor.AstVisitor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * SELECT 문을 나타내는 AST 노드.
 */
public final class SelectStatement implements Statement {

    private final SelectClause selectClause;
    private final FromClause fromClause;

    @Nullable
    private final WhereClause whereClause;

    @Nullable
    private final GroupByClause groupByClause;

    @Nullable
    private final HavingClause havingClause;

    @Nullable
    private final OrderByClause orderByClause;

    @Nullable
    private final LimitClause limitClause;
    private final SourceLocation location;

    private SelectStatement(final Builder builder) {
        this.selectClause = builder.selectClause;
        this.fromClause = builder.fromClause;
        this.whereClause = builder.whereClause;
        this.groupByClause = builder.groupByClause;
        this.havingClause = builder.havingClause;
        this.orderByClause = builder.orderByClause;
        this.limitClause = builder.limitClause;
        this.location = builder.location;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public StatementType type() {
        return StatementType.SELECT;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitSelectStatement(this);
    }

    @Override
    public SourceLocation location() {
        return location;
    }

    public SelectClause selectClause() {
        return selectClause;
    }

    public Optional<FromClause> fromClause() {
        return Optional.ofNullable(fromClause);
    }

    public Optional<WhereClause> whereClause() {
        return Optional.ofNullable(whereClause);
    }

    public Optional<GroupByClause> groupByClause() {
        return Optional.ofNullable(groupByClause);
    }

    public Optional<HavingClause> havingClause() {
        return Optional.ofNullable(havingClause);
    }

    public Optional<OrderByClause> orderByClause() {
        return Optional.ofNullable(orderByClause);
    }

    public Optional<LimitClause> limitClause() {
        return Optional.ofNullable(limitClause);
    }

    public static class Builder {
        private SelectClause selectClause;
        private FromClause fromClause;
        private WhereClause whereClause;
        private GroupByClause groupByClause;
        private HavingClause havingClause;
        private OrderByClause orderByClause;
        private LimitClause limitClause;
        private SourceLocation location = SourceLocation.UNKNOWN;

        public Builder selectClause(final SelectClause selectClause) {
            this.selectClause = selectClause;
            return this;
        }

        public Builder fromClause(final FromClause fromClause) {
            this.fromClause = fromClause;
            return this;
        }

        public Builder whereClause(final WhereClause whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public Builder groupByClause(final GroupByClause groupByClause) {
            this.groupByClause = groupByClause;
            return this;
        }

        public Builder havingClause(final HavingClause havingClause) {
            this.havingClause = havingClause;
            return this;
        }

        public Builder orderByClause(final OrderByClause orderByClause) {
            this.orderByClause = orderByClause;
            return this;
        }

        public Builder limitClause(final LimitClause limitClause) {
            this.limitClause = limitClause;
            return this;
        }

        public Builder location(final SourceLocation location) {
            this.location = location;
            return this;
        }

        public SelectStatement build() {
            validate();
            location = calculateLocation();
            return new SelectStatement(this);
        }

        private void validate() {
            if (selectClause == null) {
                throw new IllegalStateException("SELECT절은 필수입니다.");
            }
        }

        private SourceLocation calculateLocation() {
            if (location != null && location != SourceLocation.UNKNOWN) {
                return location;
            }

            final SourceLocation start = selectClause.location();
            final SourceLocation end = findLastClauseLocation();

            return start.merge(end);
        }

        /**
         * SQL 문에서 가장 마지막에 위치한 절의 위치를 찾습니다.
         * SQL 실행 순서의 역순으로 검사합니다: LIMIT → ORDER BY → HAVING → GROUP BY → WHERE → FROM
         */
        private SourceLocation findLastClauseLocation() {
            // SQL 절의 역순으로 배열 생성 (마지막부터 검사)
            final Clause[] clausesInReverseOrder = {
                    limitClause,      // LIMIT (가장 마지막)
                    orderByClause,    // ORDER BY
                    havingClause,     // HAVING
                    groupByClause,    // GROUP BY
                    whereClause,      // WHERE
                    fromClause        // FROM (가장 처음)
            };

            for (final Clause clause : clausesInReverseOrder) {
                if (clause != null) {
                    return clause.location();
                }
            }

            throw new IllegalStateException("SQL 절의 위치를 찾을 수 없습니다.");
        }
    }
}
