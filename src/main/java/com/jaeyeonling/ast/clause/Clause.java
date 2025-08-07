package com.jaeyeonling.ast.clause;

import com.jaeyeonling.ast.AstNode;

/**
 * SQL 절(clause)을 나타내는 마커 인터페이스.
 * SELECT, FROM, WHERE 등 모든 절은 이 인터페이스를 구현합니다.
 */
public interface Clause extends AstNode {

    /**
     * 절의 타입을 반환합니다.
     *
     * @return 절 타입
     */
    ClauseType clauseType();
}
