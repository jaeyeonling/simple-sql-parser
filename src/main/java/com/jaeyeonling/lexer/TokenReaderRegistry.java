package com.jaeyeonling.lexer;

import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.TokenReader;
import com.jaeyeonling.lexer.reader.decimal.DecimalTokenReader;
import com.jaeyeonling.lexer.reader.identifier.IdentifierTokenReader;
import com.jaeyeonling.lexer.reader.integer.IntegerTokenReader;
import com.jaeyeonling.lexer.reader.operator.OperatorTokenReader;
import com.jaeyeonling.lexer.reader.string.StringTokenReader;

import java.util.List;
import java.util.Optional;

/**
 * TokenReader 레지스트리 - 리더들을 관리하고 적절한 리더를 찾습니다.
 * - 리더들의 우선순위 관리
 * - 입력에 적합한 리더 탐색
 */
final class TokenReaderRegistry {

    private final List<TokenReader> readers;

    TokenReaderRegistry(final List<TokenReader> readers) {
        this.readers = readers;
    }

    /**
     * 기본 TokenReader들로 레지스트리를 생성합니다.
     */
    static TokenReaderRegistry createDefault() {
        return new TokenReaderRegistry(createDefaultReaders());
    }

    /**
     * 토큰 리더들을 생성합니다.
     * 순서가 중요합니다 - 더 구체적인 패턴을 먼저 확인해야 합니다.
     */
    private static List<TokenReader> createDefaultReaders() {
        return List.of(
                new StringTokenReader(),      // 문자열 리터럴 ('...')
                new DecimalTokenReader(),     // 소수 (3.14) - 정수보다 먼저 체크
                new IntegerTokenReader(),     // 정수 (123)
                new IdentifierTokenReader(),  // 식별자와 키워드 (SELECT, table_name)
                new OperatorTokenReader()     // 연산자와 심볼 (=, <=, +, -, *)
        );
    }

    /**
     * 현재 입력을 읽을 수 있는 첫 번째 리더를 찾습니다.
     */
    Optional<TokenReader> findReader(final CharStream charStream) {
        return readers.stream()
                .filter(reader -> reader.canRead(charStream))
                .findFirst();
    }
}
