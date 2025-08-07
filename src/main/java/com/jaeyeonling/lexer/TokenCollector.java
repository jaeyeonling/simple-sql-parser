package com.jaeyeonling.lexer;

import com.jaeyeonling.exception.LexicalException;
import com.jaeyeonling.lexer.reader.CharStream;
import com.jaeyeonling.lexer.reader.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * 토큰 수집을 담당하는 클래스
 * - 입력 스트림에서 토큰들을 수집
 * - EOF 토큰 자동 추가
 * - 예외 처리
 */
final class TokenCollector {

    private final CharStream charStream;
    private final WhitespaceSkipper whitespaceSkipper;
    private final TokenReaderRegistry readerRegistry;
    private final List<Token> tokens;

    TokenCollector(
            final CharStream charStream,
            final WhitespaceSkipper whitespaceSkipper,
            final TokenReaderRegistry readerRegistry
    ) {
        this.charStream = charStream;
        this.whitespaceSkipper = whitespaceSkipper;
        this.readerRegistry = readerRegistry;
        this.tokens = new ArrayList<>();
    }

    /**
     * 모든 토큰을 수집하고 반환합니다.
     */
    List<Token> collectAllTokens() {
        collectTokensUntilEnd();
        appendEofToken();
        return tokens;
    }

    private void collectTokensUntilEnd() {
        while (hasMoreTokens()) {
            collectNextToken();
        }
    }

    private boolean hasMoreTokens() {
        whitespaceSkipper.skipAll();
        return !charStream.isAtEnd();
    }

    private void collectNextToken() {
        final Token token = readNextToken();
        tokens.add(token);
    }

    private Token readNextToken() {
        return readerRegistry
                .findReader(charStream)
                .map(reader -> reader.read(charStream))
                .orElseThrow(this::createUnexpectedCharacterError);
    }

    private LexicalException createUnexpectedCharacterError() {
        final char unexpected = charStream.current();
        final Position position = charStream.position();

        return new LexicalException(
                String.format("""
                                SQL 구문에서 사용할 수 없는 문자입니다: '%c'
                                위치: %d행 %d열
                                허용되는 문자: 알파벳, 숫자, '_', '.', ',', '(', ')', '*', '+', '-', '=', '<', '>', '!', 작은따옴표""",
                        unexpected, position.line(), position.column())
        );
    }

    private void appendEofToken() {
        tokens.add(createEofToken());
    }

    private Token createEofToken() {
        final Position position = charStream.position();
        return new Token(
                TokenType.EOF,
                "",
                position.line(),
                position.column(),
                position.index(),
                position.index()
        );
    }
}
