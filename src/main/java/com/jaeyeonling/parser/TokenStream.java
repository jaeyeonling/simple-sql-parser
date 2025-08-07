package com.jaeyeonling.parser;

import com.jaeyeonling.exception.SyntaxException;
import com.jaeyeonling.lexer.Token;
import com.jaeyeonling.lexer.TokenType;

import java.util.List;
import java.util.Optional;

/**
 * 토큰 스트림을 관리하는 클래스.
 */
public final class TokenStream {

    private final List<Token> tokens;
    private int current = 0;

    public TokenStream(final List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * 현재 토큰이 주어진 타입들 중 하나와 일치하는지 확인하고,
     * 일치하면 다음 토큰으로 이동합니다.
     */
    public boolean advanceIfMatch(final TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * 현재 토큰이 주어진 타입인지 확인합니다.
     * 일치하지 않으면 예외를 발생시킵니다.
     */
    public Token consume(
            final TokenType type,
            final String message
    ) {
        if (check(type)) {
            return advance();
        }

        throw new SyntaxException(message, peek());
    }

    /**
     * 현재 토큰이 주어진 타입인지 확인합니다.
     */
    public boolean check(final TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type() == type;
    }

    /**
     * 다음 토큰으로 이동합니다.
     */
    public Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    /**
     * 스트림의 끝에 도달했는지 확인합니다.
     */
    public boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    /**
     * 현재 토큰을 반환합니다.
     */
    public Token peek() {
        return tokens.get(current);
    }

    /**
     * 현재 위치에서 offset만큼 떨어진 토큰을 반환합니다.
     * 위치를 변경하지 않습니다.
     *
     * @param offset 현재 위치로부터의 거리 (1이면 다음 토큰, 2면 그 다음 토큰)
     * @return 해당 위치의 토큰, 범위를 벗어나면 null
     */
    public Token peekAt(final int offset) {
        int targetIndex = current + offset;
        if (targetIndex >= 0 && targetIndex < tokens.size()) {
            return tokens.get(targetIndex);
        }
        return null;
    }

    /**
     * 이전 토큰을 반환합니다.
     */
    public Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * 현재 위치의 토큰을 반환합니다.
     */
    public Token current() {
        return tokens.get(current);
    }

    /**
     * 현재 토큰이 키워드인지 확인합니다.
     */
    public boolean isKeyword(final Token token) {
        return token.type().isKeyword();
    }

    /**
     * 선택적 별칭(alias)을 파싱합니다.
     * AS 키워드가 있으면 그 다음 식별자를 별칭으로 사용하고,
     * AS가 없어도 예약어가 아닌 식별자가 있으면 별칭으로 사용합니다.
     *
     * @param errorMessageForAs AS 키워드 다음에 식별자가 없을 때 사용할 에러 메시지
     * @return 별칭 문자열
     */
    public Optional<String> parseOptionalAlias(final String errorMessageForAs) {
        // AS alias
        if (advanceIfMatch(TokenType.AS)) {
            final Token aliasToken = consume(TokenType.IDENTIFIER, errorMessageForAs);
            return Optional.of(aliasToken.value());
        }

        // AS 없이 별칭이 올 수 있음 (예약어가 아닌 경우)
        if (check(TokenType.IDENTIFIER) && !isKeyword(peek())) {
            return Optional.of(advance().value());
        }

        return Optional.empty();
    }
}
