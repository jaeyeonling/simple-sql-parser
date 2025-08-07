package com.jaeyeonling.lexer.reader.operator;

import com.jaeyeonling.lexer.TokenType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 단일 문자 연산자 관리 클래스
 * - 복합 연산자로 확장 가능한 문자는 제외 (<, >, =, !)
 */
final class SingleCharOperators {

    private static final Map<Character, TokenType> singleOperators = buildSingleOperators();

    private static Map<Character, TokenType> buildSingleOperators() {
        return Arrays.stream(TokenType.values())
                .filter(TokenType::isSingleCharSymbol)
                .filter(type -> isNotCompoundOperatorStart(type.asChar()))
                .collect(Collectors.toUnmodifiableMap(
                        TokenType::asChar,
                        type -> type
                ));
    }

    /**
     * 복합 연산자의 시작이 될 수 있는 문자인지 확인
     * !=, <=, >=, <> 등의 첫 문자
     */
    private static boolean isCompoundOperatorStart(final char c) {
        return c == '<' || c == '>' || c == '=' || c == '!';
    }

    private static boolean isNotCompoundOperatorStart(final char c) {
        return !isCompoundOperatorStart(c);
    }

    static Optional<TokenType> find(final char c) {
        return Optional.ofNullable(singleOperators.get(c));
    }
}
