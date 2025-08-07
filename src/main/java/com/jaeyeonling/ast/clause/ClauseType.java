package com.jaeyeonling.ast.clause;

/**
 * SQL 절 타입을 나타내는 열거형
 */
public enum ClauseType {
    SELECT,
    FROM,
    WHERE,
    GROUP_BY,
    HAVING,
    ORDER_BY,
    LIMIT
}
