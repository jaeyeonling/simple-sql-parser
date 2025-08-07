package com.jaeyeonling.lexer;

import com.jaeyeonling.lexer.reader.CharStream;

import java.util.List;

/**
 * SQL 렉서 - 문자열을 토큰의 리스트로 변환합니다.
 */
public final class SqlLexer {

    private final TokenCollector tokenCollector;

    public SqlLexer(final String input) {
        final CharStream charStream = new CharStream(input);
        final WhitespaceSkipper whitespaceSkipper = new WhitespaceSkipper(charStream);
        final TokenReaderRegistry readerRegistry = TokenReaderRegistry.createDefault();

        this.tokenCollector = new TokenCollector(
                charStream,
                whitespaceSkipper,
                readerRegistry
        );
    }

    /**
     * 입력 문자열을 토큰화합니다.
     *
     * @return 토큰 리스트 (마지막에 EOF 토큰 포함)
     */
    public List<Token> tokenize() {
        return tokenCollector.collectAllTokens();
    }
}
