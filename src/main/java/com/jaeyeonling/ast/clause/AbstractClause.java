package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.SourceLocation;

/**
 * 모든 SQL 절의 공통 기능을 제공하는 추상 클래스.
 */
public sealed abstract class AbstractClause
        implements Clause
        permits FromClause, GroupByClause, HavingClause, LimitClause, OrderByClause, SelectClause, WhereClause {

    protected final SourceLocation location;

    protected AbstractClause(final SourceLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("절의 위치 정보는 null일 수 없습니다.");
        }
        this.location = location;
    }

    @Override
    public SourceLocation location() {
        return location;
    }
}
