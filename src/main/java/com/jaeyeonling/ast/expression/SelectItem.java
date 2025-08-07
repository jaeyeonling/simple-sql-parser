package com.jaeyeonling.ast.expression;

import com.jaeyeonling.ast.AstNode;

import java.util.Optional;

/**
 * SELECT 절의 개별 항목을 나타냅니다.
 * 예: column, column AS alias, *, table.* 등
 */
public interface SelectItem extends AstNode {

    /**
     * 별칭(alias)을 반환합니다.
     *
     * @return 별칭이 있으면 Optional로 감싸서 반환, 없으면 empty
     */
    Optional<String> alias();

    /**
     * SelectItem의 타입을 반환합니다.
     *
     * @return SelectItem 타입
     */
    SelectItemType selectItemType();
}
