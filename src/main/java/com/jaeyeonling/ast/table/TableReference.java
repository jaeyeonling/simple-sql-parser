package com.jaeyeonling.ast.table;

import com.jaeyeonling.ast.AstNode;

import java.util.Optional;

/**
 * FROM 절에서 참조되는 테이블을 나타내는 인터페이스.
 * 단일 테이블, JOIN, 서브쿼리 등을 포함합니다.
 */
public interface TableReference extends AstNode {

    /**
     * 테이블 별칭을 반환합니다.
     *
     * @return 별칭이 있으면 Optional로 감싸서 반환
     */
    Optional<String> alias();

    /**
     * TableReference의 타입을 반환합니다.
     *
     * @return TableReference 타입
     */
    TableReferenceType tableReferenceType();

    /**
     * TableReference 타입을 나타내는 열거형
     */
    enum TableReferenceType {
        TABLE,          // 단일 테이블
        JOIN,           // JOIN 표현식
        SUBQUERY        // 서브쿼리
    }
}
