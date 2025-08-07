package com.jaeyeonling.ast;

import com.jaeyeonling.visitor.AstVisitor;

/**
 * AST(Abstract Syntax Tree)의 모든 노드가 구현해야 하는 기본 인터페이스.
 * Visitor 패턴을 지원하여 AST 순회와 처리를 가능하게 합니다.
 */
public interface AstNode {

    /**
     * Visitor 패턴을 위한 accept 메서드.
     * 각 노드는 이 메서드를 통해 visitor를 받아들입니다.
     *
     * @param visitor AST를 방문할 visitor
     * @param <T>     visitor가 반환하는 타입
     * @return visitor의 처리 결과
     */
    <T> T accept(AstVisitor<T> visitor);

    /**
     * 노드의 위치 정보를 반환합니다.
     *
     * @return 소스 코드에서의 위치 정보
     */
    SourceLocation location();
}
