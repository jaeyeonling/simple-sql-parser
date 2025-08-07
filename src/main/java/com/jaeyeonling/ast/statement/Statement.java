package com.jaeyeonling.ast.statement;

import com.jaeyeonling.ast.AstNode;

/**
 * SQL 문장을 나타내는 마커 인터페이스.
 * 모든 SQL 문장(SELECT, INSERT, UPDATE 등)은 이 인터페이스를 구현합니다.
 */
public interface Statement extends AstNode {

    /**
     * Statement의 타입을 반환합니다.
     *
     * @return Statement 타입
     */
    StatementType type();

    /**
     * SQL 문장 타입을 나타내는 열거형
     */
    enum StatementType {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }
}
