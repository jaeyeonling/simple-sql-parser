package com.jaeyeonling.lexer.reader.identifier;

import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.lexer.character.IdentifierChar;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 식별자 또는 키워드를 처리하는 내부 클래스
 * - 식별자 수집과 키워드 판별을 분리
 */
record IdentifierOrKeyword(
        Position position,
        String text,
        int endIndex
) {

    static IdentifierOrKeyword from(final CharStream charStream) {
        final Position startPosition = charStream.position();
        final String identifier = collectIdentifier(charStream);
        final int endIndex = charStream.currentIndex();
        return new IdentifierOrKeyword(startPosition, identifier, endIndex);
    }

    private static String collectIdentifier(final CharStream charStream) {
        final StringBuilder result = new StringBuilder();

        // Collect remaining characters
        do {
            result.append(charStream.consume());
        } while (!charStream.isAtEnd() && IdentifierChar.isIdentifierChar(charStream.current()));

        return result.toString();
    }

    Token toToken() {
        return SqlKeywords.findKeyword(text)
                .map(this::createKeywordToken)
                .orElseGet(this::createIdentifierToken);
    }

    private Token createKeywordToken(final TokenType keywordType) {
        return new Token(
                keywordType,
                text.toUpperCase(),
                position.line(),
                position.column(),
                position.index(),
                endIndex
        );
    }

    private Token createIdentifierToken() {
        return new Token(
                TokenType.IDENTIFIER,
                text,
                position.line(),
                position.column(),
                position.index(),
                endIndex
        );
    }

    /**
     * SQL 키워드 관리 클래스
     */
    private static class SqlKeywords {
        // TokenType enum에서 키워드만 추출하여 맵 생성
        private static final Map<String, TokenType> keywords = buildKeywords();

        private static Map<String, TokenType> buildKeywords() {
            return Arrays.stream(TokenType.values())
                    .filter(TokenType::isKeyword)
                    .collect(Collectors.toUnmodifiableMap(
                            type -> type.symbol().toUpperCase(),
                            type -> type
                    ));
        }

        static Optional<TokenType> findKeyword(final String text) {
            return Optional.ofNullable(keywords.get(text.toUpperCase()));
        }
    }
}
