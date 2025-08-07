package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;
import com.jaeyeonling.visitor.AstVisitor;

import java.util.Optional;

/**
 * LIMIT 절을 나타내는 AST 노드.
 */
public final class LimitClause extends AbstractClause {

    private final Limit limit;
    private final Offset offset;

    private LimitClause(
            final Limit limit,
            final Offset offset,
            final SourceLocation location
    ) {
        super(location);
        this.limit = limit;
        this.offset = offset;
    }

    public LimitClause(
            final int limit,
            final Integer offset,
            final SourceLocation location
    ) {
        this(new Limit(limit), new Offset(offset), location);
    }

    public LimitClause(
            final int limit,
            final SourceLocation location
    ) {
        this(limit, null, location);
    }

    @Override
    public ClauseType clauseType() {
        return ClauseType.LIMIT;
    }

    @Override
    public <T> T accept(final AstVisitor<T> visitor) {
        return visitor.visitLimitClause(this);
    }


    public int limit() {
        return limit.value;
    }

    public Optional<Integer> offset() {
        return offset.orNull();
    }

    private record Limit(int value) {
        Limit {
            if (value < 0) {
                throw new IllegalArgumentException("Limit은 0보다 커야 합니다.");
            }
        }
    }

    private record Offset(Integer value) {
        Offset {
            if (value != null && value < 0) {
                throw new IllegalArgumentException("Offset은 0보다 커야 합니다.");
            }
        }

        public Optional<Integer> orNull() {
            return Optional.ofNullable(value);
        }
    }
}
