package com.jaeyeonling.parser.expression;

import com.jaeyeonling.ast.expression.Expression;
import com.jaeyeonling.parser.TokenStream;

/**
 * 연산자 파싱을 담당하는 인터페이스.
 * 각 연산자별로 이 인터페이스를 구현하여 파싱 로직을 분리합니다.
 */
public interface OperatorParser {

    /**
     * 현재 토큰 스트림에서 이 파서가 처리할 수 있는 연산자가 있는지 확인합니다.
     *
     * @param tokenStream 토큰 스트림
     * @return 처리 가능하면 true
     */
    boolean canParse(TokenStream tokenStream);

    /**
     * 표현식을 파싱합니다.
     *
     * @param tokenStream 토큰 스트림
     * @param left        왼쪽 피연산자 (이미 파싱됨)
     * @return 파싱된 표현식
     */
    Expression parse(TokenStream tokenStream, Expression left);

    /**
     * 이 파서의 우선순위를 반환합니다.
     * 값이 작을수록 먼저 처리됩니다.
     *
     * @return 우선순위
     */
    OperatorPriority priority();
}
