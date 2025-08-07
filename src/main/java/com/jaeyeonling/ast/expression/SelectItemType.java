package com.jaeyeonling.ast.expression;

/**
 * SelectItem 타입을 나타내는 열거형
 */
public enum SelectItemType {
    COLUMN,         // 단일 컬럼
    ALL_COLUMNS,    // *
    TABLE_ALL,      // table.*
    EXPRESSION      // 표현식
}
