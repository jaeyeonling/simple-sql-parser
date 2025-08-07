package com.jaeyeonling.lexer.reader;

import com.jaeyeonling.lexer.Token;

/**
 * 토큰을 읽는 전략 인터페이스.
 * 각 토큰 타입별로 구체적인 읽기 전략을 구현합니다.
 */
public interface TokenReader {

    /**
     * 현재 위치에서 토큰을 읽을 수 있는지 확인합니다.
     *
     * @param charStream 문자 스트림
     * @return 토큰을 읽을 수 있으면 true
     */
    boolean canRead(CharStream charStream);

    /**
     * 토큰을 읽습니다.
     *
     * @param charStream 문자 스트림
     * @return 읽은 토큰
     */
    Token read(CharStream charStream);
}
