package com.jaeyeonling.lexer;

import com.jaeyeonling.exception.SqlParseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.jaeyeonling.lexer.TokenType.COMMA;
import static com.jaeyeonling.lexer.TokenType.DOT;
import static com.jaeyeonling.lexer.TokenType.EOF;
import static com.jaeyeonling.lexer.TokenType.FROM;
import static com.jaeyeonling.lexer.TokenType.GREATER_THAN_OR_EQUALS;
import static com.jaeyeonling.lexer.TokenType.IDENTIFIER;
import static com.jaeyeonling.lexer.TokenType.LPAREN;
import static com.jaeyeonling.lexer.TokenType.RPAREN;
import static com.jaeyeonling.lexer.TokenType.SELECT;
import static com.jaeyeonling.lexer.TokenType.STAR;
import static com.jaeyeonling.lexer.TokenType.WHERE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * SqlLexer 단위 테스트
 */
class SqlLexerTest {

    @Test
    @DisplayName("빈 문자열을 토큰화하면 EOF 토큰만 반환한다")
    void testEmptyString() {
        // given
        final SqlLexer lexer = new SqlLexer("");

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(1);
        assertThat(tokens.getFirst().type()).isEqualTo(TokenType.EOF);
    }

    @Test
    @DisplayName("공백만 있는 문자열을 토큰화하면 EOF 토큰만 반환한다")
    void testWhitespaceOnly() {
        // given
        final SqlLexer lexer = new SqlLexer("   \t\n\r  ");

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(1);
        assertThat(tokens.getFirst().type()).isEqualTo(TokenType.EOF);
    }

    @ParameterizedTest
    @DisplayName("SQL 키워드를 올바르게 토큰화한다")
    @CsvSource({
            "SELECT",
            "FROM",
            "WHERE",
            "AS",
            "DISTINCT",
            "GROUP",
            "BY",
            "HAVING",
            "ORDER",
            "LIMIT",
            "OFFSET",
            "ASC",
            "DESC",
            "AND",
            "OR",
            "NOT",
            "IN",
            "BETWEEN",
            "LIKE",
            "NULL",
            "TRUE",
            "FALSE"
    })
    void testKeywordTokenization(final String keyword) {
        // given
        final SqlLexer lexer = new SqlLexer(keyword);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(2); // keyword + EOF
        final TokenType expectedType = TokenType.valueOf(keyword);
        assertThat(tokens.getFirst().type()).isEqualTo(expectedType);
        assertThat(tokens.getFirst().value()).isEqualTo(keyword);
    }

    @ParameterizedTest
    @DisplayName("키워드는 대소문자를 구분하지 않는다")
    @ValueSource(strings = {"select", "SELECT", "Select", "SeLeCt"})
    void testKeywordCaseInsensitive(final String input) {
        // given
        final SqlLexer lexer = new SqlLexer(input);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens.getFirst().type()).isEqualTo(TokenType.SELECT);
        assertThat(tokens.getFirst().value()).isEqualTo(input.toUpperCase());
    }

    @Test
    @DisplayName("식별자를 올바르게 토큰화한다")
    void testIdentifierTokenization() {
        // given
        final String sql = "users user_id userName _private column123";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(6); // 5 identifiers + EOF
        assertThat(tokens.subList(0, 5))
                .extracting(Token::type)
                .containsOnly(TokenType.IDENTIFIER);
        assertThat(tokens.subList(0, 5))
                .extracting(Token::value)
                .containsExactly("users", "user_id", "userName", "_private", "column123");
    }

    @Test
    @DisplayName("정수 리터럴을 올바르게 토큰화한다")
    void testIntegerLiteralTokenization() {
        // given
        final String sql = "0 123 999999";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(4); // 3 integers + EOF
        assertThat(tokens.subList(0, 3))
                .extracting(Token::type)
                .containsOnly(TokenType.INTEGER);
        assertThat(tokens.subList(0, 3))
                .extracting(Token::value)
                .containsExactly("0", "123", "999999");
    }

    @Test
    @DisplayName("소수 리터럴을 올바르게 토큰화한다")
    void testDecimalLiteralTokenization() {
        // given
        final String sql = "3.14 0.5 123.456 .789";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(5); // 4 decimals + EOF
        assertThat(tokens.subList(0, 4))
                .extracting(Token::type)
                .containsOnly(TokenType.DECIMAL);
        assertThat(tokens.subList(0, 4))
                .extracting(Token::value)
                .containsExactly("3.14", "0.5", "123.456", ".789");
    }

    @Test
    @DisplayName("문자열 리터럴을 올바르게 토큰화한다")
    void testStringLiteralTokenization() {
        // given
        final String sql = "'hello' 'world' '123' ''";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).hasSize(5); // 4 strings + EOF
        assertThat(tokens.subList(0, 4))
                .extracting(Token::type)
                .containsOnly(TokenType.STRING);
        assertThat(tokens.subList(0, 4))
                .extracting(Token::value)
                .containsExactly("hello", "world", "123", "");
    }

    @Test
    @DisplayName("이스케이프된 따옴표가 포함된 문자열을 올바르게 처리한다")
    void testStringWithEscapedQuote() {
        // given
        final String sql = "'It\\'s me'";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens.getFirst().value()).isEqualTo("It's me");
    }

    @Test
    @DisplayName("종료되지 않은 문자열은 예외를 발생시킨다")
    void testUnterminatedString() {
        // given
        final String sql = "'unterminated string";
        final SqlLexer lexer = new SqlLexer(sql);

        // when & then
        assertThatThrownBy(lexer::tokenize)
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("종료되지 않은 문자열");
    }

    @ParameterizedTest
    @DisplayName("연산자를 올바르게 토큰화한다")
    @CsvSource({
            "'=', EQUALS",
            "'!=', NOT_EQUALS",
            "'<', LESS_THAN",
            "'<=', LESS_THAN_OR_EQUALS",
            "'>', GREATER_THAN",
            "'>=', GREATER_THAN_OR_EQUALS"
    })
    void testOperatorTokenization(
            final String operator,
            final String expectedTypeName
    ) {
        // given
        final SqlLexer lexer = new SqlLexer(operator);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        final TokenType expectedType = TokenType.valueOf(expectedTypeName);
        assertThat(tokens.getFirst().type()).isEqualTo(expectedType);
        assertThat(tokens.getFirst().value()).isEqualTo(operator);
    }

    @ParameterizedTest
    @DisplayName("기호를 올바르게 토큰화한다")
    @CsvSource({
            "'*', STAR",
            "',', COMMA",
            "'.', DOT",
            "'(', LPAREN",
            "')', RPAREN"
    })
    void testSymbolTokenization(
            final String symbol,
            final String expectedTypeName
    ) {
        // given
        final SqlLexer lexer = new SqlLexer(symbol);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        final TokenType expectedType = TokenType.valueOf(expectedTypeName);
        assertThat(tokens.getFirst().type()).isEqualTo(expectedType);
        assertThat(tokens.getFirst().value()).isEqualTo(symbol);
    }

    @Test
    @DisplayName("한 줄 주석을 무시한다")
    void testSingleLineComment() {
        // given
        final String sql = "SELECT -- this is a comment\nFROM users";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).extracting(Token::type)
                .containsExactly(TokenType.SELECT, TokenType.FROM, TokenType.IDENTIFIER, TokenType.EOF);
    }

    @Test
    @DisplayName("복합 SQL 문을 올바르게 토큰화한다")
    void testComplexSqlTokenization() {
        // given
        final String sql = "SELECT u.name, COUNT(*) FROM users u WHERE age >= 18";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        assertThat(tokens).extracting(Token::type).containsExactly(
                SELECT,           // SELECT
                IDENTIFIER,       // u
                DOT,             // .
                IDENTIFIER,       // name
                COMMA,           // ,
                IDENTIFIER,       // COUNT
                LPAREN,          // (
                STAR,            // *
                RPAREN,          // )
                FROM,            // FROM
                IDENTIFIER,       // users
                IDENTIFIER,       // u
                WHERE,           // WHERE
                IDENTIFIER,       // age
                GREATER_THAN_OR_EQUALS, // >=
                TokenType.INTEGER,         // 18
                EOF              // EOF
        );
    }

    @Test
    @DisplayName("토큰 위치 정보가 올바르게 설정된다")
    void testTokenLocation() {
        // given
        final String sql = "SELECT name\nFROM users";
        final SqlLexer lexer = new SqlLexer(sql);

        // when
        final List<Token> tokens = lexer.tokenize();

        // then
        final Token selectToken = tokens.getFirst();
        assertThat(selectToken.line()).isEqualTo(1);
        assertThat(selectToken.column()).isEqualTo(1);

        final Token fromToken = tokens.get(2);
        assertThat(fromToken.line()).isEqualTo(2);
        assertThat(fromToken.column()).isEqualTo(1);
    }

    @Test
    @DisplayName("예상치 못한 문자는 예외를 발생시킨다")
    void testUnexpectedCharacter() {
        // given
        final String sql = "SELECT @ FROM users";
        final SqlLexer lexer = new SqlLexer(sql);

        // when & then
        assertThatThrownBy(lexer::tokenize)
                .isInstanceOf(SqlParseException.class)
                .hasMessageContaining("SQL 구문에서 사용할 수 없는 문자입니다: '@'");
    }
}
