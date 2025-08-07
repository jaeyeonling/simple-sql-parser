package com.jaeyeonling.lexer.reader.operator;

import com.jaeyeonling.exception.LexicalException;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

/**
 * 연산자 파싱 로직을 담당하는 클래스
 */
record OperatorParser(
        CharStream charStream,
        Position position
) {

    Token parse() {
        final char firstChar = charStream.current();

        // Try single character operators first
        return SingleCharOperators.find(firstChar)
                .map(this::consumeAndCreateToken)
                .orElseGet(() -> parseComplexOperator(firstChar));
    }

    private Token consumeAndCreateToken(final TokenType type) {
        final String value = String.valueOf(charStream.consume());
        return createToken(type, value);
    }

    private Token parseComplexOperator(final char firstChar) {
        charStream.advance(); // Consume first character

        return switch (firstChar) {
            case '!' -> parseExclamation();
            case '<' -> parseLessThan();
            case '>' -> parseGreaterThan();
            case '=' -> createToken(TokenType.EQUALS, "=");
            default -> throw unexpectedCharacter(firstChar);
        };
    }

    private Token parseExclamation() {
        if (charStream.advanceIfMatch('=')) {
            return createToken(TokenType.NOT_EQUALS, "!=");
        }
        throw unexpectedCharacter('!');
    }

    private Token parseLessThan() {
        if (charStream.advanceIfMatch('=')) {
            return createToken(TokenType.LESS_THAN_OR_EQUALS, "<=");
        }
        if (charStream.advanceIfMatch('>')) {
            return createToken(TokenType.NOT_EQUALS, "<>");
        }
        return createToken(TokenType.LESS_THAN, "<");
    }

    private Token parseGreaterThan() {
        if (charStream.advanceIfMatch('=')) {
            return createToken(TokenType.GREATER_THAN_OR_EQUALS, ">=");
        }
        return createToken(TokenType.GREATER_THAN, ">");
    }

    private Token createToken(
            final TokenType type,
            final String value
    ) {
        return new Token(
                type,
                value,
                position.line(),
                position.column(),
                position.index(),
                position.index() + value.length()
        );
    }

    private LexicalException unexpectedCharacter(final char c) {
        return new LexicalException(
                String.format(
                        """
                                연산자로 사용할 수 없는 문자입니다.
                                문자: '%c'
                                위치: %d행 %d열
                                연산자는 완전한 형태여야 합니다. 예: !=, <=, >=""",
                        c, position.line(), position.column())
        );
    }
}
